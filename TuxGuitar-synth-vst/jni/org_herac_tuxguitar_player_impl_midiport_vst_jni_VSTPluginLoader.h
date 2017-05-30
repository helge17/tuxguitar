#ifndef _Included_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPluginLoader
#define _Included_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPluginLoader

void VSTPluginLoad( void **plugin , const char *file );

void VSTPluginFree( void **plugin );

void VSTPluginMain( void **plugin , AEffect **effect , audioMasterCallback callback );

#endif /* _Included_org_herac_tuxguitar_player_impl_midiport_vst_jni_VSTPluginLoader */
