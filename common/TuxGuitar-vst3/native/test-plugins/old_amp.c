/* Old Amp: stands for a VST 2 plugin placed in the VST3 folders. It has the VST 2 entry points and no VST3 factory;
   it creates no plugin. */
#ifdef _WIN32
#define OLD_AMP_EXPORT __declspec(dllexport)
#else
#define OLD_AMP_EXPORT __attribute__((visibility("default")))
#endif

OLD_AMP_EXPORT void* VSTPluginMain(void* hostCallback) {
	(void) hostCallback;
	return 0;
}

OLD_AMP_EXPORT void* main_plugin(void* hostCallback) {
	return VSTPluginMain(hostCallback);
}
