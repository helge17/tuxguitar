Summary: A multitrack tablature editor and player written in Java-SWT
Name: tuxguitar
Version: 1.0
Release: 5%{?dist}
URL: http://www.tuxguitar.com.ar
Source0: http://downloads.sourceforge.net/%{name}/%{name}-src-%{version}.tar.gz
Source9: %{name}.desktop
License: LGPLv2+
Group: Applications/Multimedia
BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)
# The package java-icedtea is not available on F-8 for ppc & ppc64.
# https://bugzilla.redhat.com/show_bug.cgi?id=464843
%if "%{fedora}" == "8"
ExcludeArch: ppc ppc64
%endif
Requires: java >= 1.7
Requires:  jpackage-utils
BuildRequires: alsa-lib-devel
BuildRequires: ant
BuildRequires: desktop-file-utils
BuildRequires: fluidsynth-devel
BuildRequires: java-devel >= 1.7
BuildRequires: jpackage-utils
BuildRequires: libswt3-gtk2

%description
TuxGuitar is a guitar tablature editor with player support through 
midi. It can display scores and multitrack tabs. Various features 
TuxGuitar provides include autoscrolling while playing, note 
duration management, bend/slide/vibrato/hammer-on/pull-off effects,
support for tuplets, time signature management, tempo management,
gp3/gp4/gp5 import and export.

%prep
%setup -q -n %{name}-src-%{version}

# The following issues were discussed in:
# http://www.tuxguitar.com.ar/forum/4/817/need-help-with-packaging-for-fedora/
# Prevent static library paths to be built in TuxGuitar.jar (META-INF/MANIFEST.MF)
sed -i 's/<attribute name="Class-Path" value="${lib.swt.jar} ${dist.share.path}"\/>//' TuxGuitar/build.xml
# Export the library path during runtime instead
sed -i 's|env_$|env_\nexport CLASSPATH=$CLASSPATH:%{_libdir}/java/swt.jar\n|' misc/tuxguitar.sh

# Disable the pdf plugin that depends on "iText" which is currently not available on Fedora
sed -i 's/TuxGuitar-pdf \\/\\/g' Makefile
sed -i 's/.\/TuxGuitar-pdf\/tuxguitar-pdf.jar \\/\\/g' Makefile
# Change /lib to %%{_lib}
sed -i 's/\/lib\//\/%{_lib}\//g' Makefile
sed -i 's/\/lib\//\/%{_lib}\//g' misc/tuxguitar.sh
sed -i 's/\/lib\//\/%{_lib}\//g' TuxGuitar/xml/build-linux.xml
# Don't strip the binaries during %%install
sed -i 's/install -s/install -m 755/g' Makefile
# Remove pre-shipped binaries
find -name .DS_Store -exec rm {} \;

%build
# Does not compile with %%{?_smp_mflags}. Build using openjdk. 
# Note that the lib directory is /usr/lib for openjdk, regardless of the architecture.
%if "%{fedora}" == "8"
 %define openjdkdir /usr/lib/jvm/java-icedtea
%else
 %define openjdkdir /usr/lib/jvm/java-openjdk
%endif

make SWT_JAR=%{_libdir}/java/swt.jar CFLAGS="${RPM_OPT_FLAGS} -I%{openjdkdir}/include -I%{openjdkdir}/include/linux -fPIC"

%install
rm -rf $RPM_BUILD_ROOT
make DESTDIR=$RPM_BUILD_ROOT install
make DESTDIR=$RPM_BUILD_ROOT install-linux

# Makefile does not install the plugins by default so install them manually
install -dm 755 $RPM_BUILD_ROOT/%{_datadir}/%{name}/plugins

for jardir in TuxGuitar-*; do
 if [ -e $jardir/*jar ]
 then 
  install -m 644 $jardir/*jar  $RPM_BUILD_ROOT/%{_datadir}/%{name}/plugins/
 fi
done

# desktop files
install -dm 755 $RPM_BUILD_ROOT/%{_datadir}/applications
install -pm 644 %{SOURCE9} $RPM_BUILD_ROOT/%{_datadir}/applications/

# icons
for dim in 16x16 24x24 32x32 48x48 64x64 96x96; do
 install -dm 755 $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/$dim/apps/%{name}.png
 install -pm 644 TuxGuitar/share/skins/Lavender/icon-$dim.png $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/$dim/apps/%{name}.png
done

# mime-type icons
install -dm 755 $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/96x96/mimetypes
install -pm 644 TuxGuitar/share/skins/Lavender/icon-96x96.png $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/96x96/mimetypes/audio-x-tuxguitar.png
install -pm 644 TuxGuitar/share/skins/Lavender/icon-96x96.png $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/96x96/mimetypes/audio-x-gtp.png
install -pm 644 TuxGuitar/share/skins/Lavender/icon-96x96.png $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/96x96/mimetypes/audio-x-ptb.png

desktop-file-install --dir $RPM_BUILD_ROOT%{_datadir}/applications --delete-original $RPM_BUILD_ROOT%{_datadir}/applications/%{name}.desktop

# mime-type file
install -dm 755 $RPM_BUILD_ROOT/%{_datadir}/mime/packages
install -pm 644 misc/%{name}.xml $RPM_BUILD_ROOT/%{_datadir}/mime/packages/

%clean
rm -rf $RPM_BUILD_ROOT

%post
update-mime-database %{_datadir}/mime  >& /dev/null ||:
touch --no-create %{_datadir}/icons/hicolor
if [ -x %{_bindir}/gtk-update-icon-cache ] ; then
  %{_bindir}/gtk-update-icon-cache --quiet %{_datadir}/icons/hicolor || :
fi

%postun
update-mime-database %{_datadir}/mime  >& /dev/null ||:
touch --no-create %{_datadir}/icons/hicolor
if [ -x %{_bindir}/gtk-update-icon-cache ] ; then
  %{_bindir}/gtk-update-icon-cache --quiet %{_datadir}/icons/hicolor || :
fi

%files
%defattr(-,root,root,-)
%doc AUTHORS ChangeLog LICENSE README COPYING

%{_libdir}/jni/*
%{_datadir}/%{name}
%{_datadir}/icons/hicolor/*/*/*
%{_datadir}/applications/%{name}.desktop
%{_datadir}/mime/packages/*.xml

%{_bindir}/%{name}

%changelog
* Mon Sep 29 2008 Orcan Ogetbil <orcanbahri[AT]yahoo[DOT]com> - 1.0-5
- Compiled the package with openjdk instead of gcj.
- ExcludeArch'ed ppc/ppc64 on F-8.

* Sun Sep 28 2008 Orcan Ogetbil <orcanbahri[AT]yahoo[DOT]com> - 1.0-4
- Added the comment about %%{?_smp_mflags}
- Used macros more extensively.
- Changed the license to LGPLv2+
- Fixed java requirement issue by requiring java >= 1.7
- Required jpackage-utils
- Removed pre-shipped binaries
- Fixed %%defattr

* Sun Sep 28 2008 Orcan Ogetbil <orcanbahri[AT]yahoo[DOT]com> - 1.0-3
- Fixed java requirement issue by requiring icedtea for F-8 and openjdk for F-9+
- Patched the source to enable the fluidsynth plugin
- Added DistTag
- Patched the source in order to pass RPM_OPT_FLAGS to gcc
- Removed ExclusiveArch

* Thu Sep 25 2008 Orcan Ogetbil <orcanbahri[AT]yahoo[DOT]com> - 1.0-2
- Added desktop-file-utils to BuildRequires.
- Replaced java-1.7.0-icedtea with java-1.6.0-openjdk in Requires.

* Wed Sep 24 2008 Orcan Ogetbil <orcanbahri[AT]yahoo[DOT]com> - 1.0-1
- Initial build.
