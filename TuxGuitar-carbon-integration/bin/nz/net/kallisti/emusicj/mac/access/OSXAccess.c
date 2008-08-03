#include <Carbon/Carbon.h>
#include <jni.h>
#include <AEDataModel.h>

#define VERSION "1.03"

/**
 * AEDesc code from SWT, os_structs.c
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 */
typedef struct AEDesc_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID descriptorType, dataHandle;
} AEDesc_FID_CACHE;

AEDesc_FID_CACHE AEDescFc;

void cacheAEDescFields(JNIEnv *env, jobject lpObject)
{
	if (AEDescFc.cached) return;
	AEDescFc.clazz = (*env)->GetObjectClass(env, lpObject);
	AEDescFc.descriptorType = (*env)->GetFieldID(env, AEDescFc.clazz, "descriptorType", "I");
	AEDescFc.dataHandle = (*env)->GetFieldID(env, AEDescFc.clazz, "dataHandle", "I");
	AEDescFc.cached = 1;
}

AEDesc *getAEDescFields(JNIEnv *env, jobject lpObject, AEDesc *lpStruct)
{
	if (!AEDescFc.cached) cacheAEDescFields(env, lpObject);
	lpStruct->descriptorType = (DescType)(*env)->GetIntField(env, lpObject, AEDescFc.descriptorType);
	lpStruct->dataHandle = (AEDataStorage)(*env)->GetIntField(env, lpObject, AEDescFc.dataHandle);
	return lpStruct;
}

void setAEDescFields(JNIEnv *env, jobject lpObject, AEDesc *lpStruct)
{
	if (!AEDescFc.cached) cacheAEDescFields(env, lpObject);
	(*env)->SetIntField(env, lpObject, AEDescFc.descriptorType, (jint)lpStruct->descriptorType);
	(*env)->SetIntField(env, lpObject, AEDescFc.dataHandle, (jint)lpStruct->dataHandle);
}

JNIEXPORT jint JNICALL Java_nz_net_kallisti_emusicj_mac_access_OSXAccess_AEGetParamDesc
	(JNIEnv *env, jclass that, jint theAppleEvent, jint theAEKeyword, 
	 jint desiredType, jobject result)
{
	AEDesc _result, *lpresult=NULL;
	
	jint rc = 0;

	if (result) if ((lpresult = getAEDescFields(env, result, &_result)) == NULL) goto fail;
	
	rc = (jint)AEGetParamDesc((const AppleEvent *)theAppleEvent, 
	  (AEKeyword)theAEKeyword,
	  (DescType)desiredType,
	  (AEDescList *)lpresult);

fail:
	if (result && lpresult) setAEDescFields(env, result, lpresult);

	return rc;
}

JNIEXPORT jstring JNICALL 
Java_nz_net_kallisti_emusicj_mac_access_OSXAccess_getVersion(
	JNIEnv		*env,
	jclass		cla )
{
	jstring	result =(*env)->NewStringUTF(env, (char *)VERSION);

	return( result );
}

JNIEXPORT jstring JNICALL
Java_nz_net_kallisti_emusicj_mac_access_OSXAccess_getDocDir(
	JNIEnv		*env,
	jclass		cla )
{
	CFURLRef docURL;
	CFStringRef docPath;
	FSRef    fsRef;
  OSErr    err = FSFindFolder(kUserDomain, kDocumentsFolderType, kDontCreateFolder, &fsRef);
  
  jstring result = 0;

	if (err == noErr) {
  	if((docURL = CFURLCreateFromFSRef( kCFAllocatorSystemDefault, &fsRef))) {
			docPath = CFURLCopyFileSystemPath(docURL, kCFURLPOSIXPathStyle);
			
			if (docPath) {
				// convert to unicode
				CFIndex strLen = CFStringGetLength( docPath );
				UniChar uniStr[ strLen ];
				CFRange strRange;
				strRange.location = 0;
				strRange.length = strLen;
				CFStringGetCharacters( docPath, strRange, uniStr );
	
				result = (*env)->NewString( env, (jchar*)uniStr, (jsize)strLen );

				CFRelease(docPath);
				
				return result;
			}
			CFRelease(docURL);
		}

		return result;
  }
}

JNIEXPORT void JNICALL
Java_nz_net_kallisti_emusicj_mac_access_OSXAccess_memmove(
	JNIEnv *env,
	jclass cla,
	jbyteArray dest,
	jint src,
	jint count)
{
	jbyte *dest1;
	
	if (dest) {
		dest1 = (*env)->GetByteArrayElements(env, dest, NULL);
		memmove((void *)dest1, (void *)src, count);
		(*env)->ReleaseByteArrayElements(env, dest, dest1, 0);
	}
}
