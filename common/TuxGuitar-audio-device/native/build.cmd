@echo off
rem Builds the native audio backend, a JNI library over PortAudio (see CMakeLists.txt): build.cmd <build directory>
rem Uses CMake, Ninja and MSVC from the environment when they are there (a Developer Command Prompt), else from the
rem newest Visual Studio with the C++ tools (found by vswhere). Without one it fails, saying what to install.
setlocal
set "SOURCE=%~dp0."
set "OUTPUT=%~1"
if "%OUTPUT%"=="" set "OUTPUT=%~dp0..\native-build"

where cl >nul 2>nul && where cmake >nul 2>nul && where ninja >nul 2>nul && goto build
set "VSWHERE=%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe"
if not exist "%VSWHERE%" goto toolchain
for /f "usebackq delims=" %%i in (`"%VSWHERE%" -latest -products * -requires Microsoft.VisualStudio.Component.VC.Tools.x86.x64 -property installationPath`) do set "VSDIR=%%i"
if not defined VSDIR goto toolchain
if not exist "%VSDIR%\VC\Auxiliary\Build\vcvars64.bat" goto toolchain
call "%VSDIR%\VC\Auxiliary\Build\vcvars64.bat" >nul 2>nul
where cmake >nul 2>nul || goto toolchain
where ninja >nul 2>nul || goto toolchain

:build
if not exist "%OUTPUT%\CMakeCache.txt" (
	cmake -S "%SOURCE%" -B "%OUTPUT%" -G Ninja -DCMAKE_BUILD_TYPE=Release || exit /b 1
)
cmake --build "%OUTPUT%" || exit /b 1
exit /b 0

:toolchain
echo Audio backend native build: no C++ toolchain found. The audio backend needs CMake 3.25+, Ninja and MSVC: 1>&2
echo install Visual Studio 2022 or later with "Desktop development with C++" (it brings CMake and Ninja), or run 1>&2
echo Maven from a Developer Command Prompt. 1>&2
exit /b 1
