#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <QtGlobal>
#include <QAction>
#include <QApplication>
#include <QCloseEvent>
#include <QDial>
#include <QDialog>
#include <QFontMetrics>
#include <QGroupBox>
#include <QGuiApplication>
#include <QHBoxLayout>
#include <QKeySequence>
#include <QLabel>
#include <QLayout>
#include <QLayoutItem>
#include <QList>
#include <QMainWindow>
#include <QMenu>
#include <QMenuBar>
#include <QObject>
#include <QPoint>
#include <QRect>
#include <QScreen>
#include <QScrollArea>
#include <QSize>
#include <QSizePolicy>
#include <QString>
#include <QStyle>
#include <QTimer>
#include <QVBoxLayout>
#include <QWidget>
#include <QtCore>
#include "LV2.h"
#include "LV2UI.h"
#include "LV2Plugin.h"
#include "LV2Instance.h"
#include "LV2Logger.h"

#define NATIVE_UI_TYPE_URI LV2_UI__Qt5UI

typedef struct {
	LV2Instance* instance;
	SuilHost* suilHost;
	SuilInstance* suilInstance;
	const LilvUI*   supported_ui;
	const LilvNode* supported_ui_type;
	bool open;

	QMainWindow* window;
	QApplication *application;
} LV2UIQt5;

class LV2MainWindow : public QMainWindow {
	public:
		LV2MainWindow(LV2UIQt5* handle) {
			LV2MainWindow::handle = handle;
		}
	private:
		LV2UIQt5* handle;
	protected:
		void closeEvent(QCloseEvent *event) {
			event->ignore();

			QMainWindow::setVisible(false);

			handle->open = false;
		}
};

LV2UIQt5* LV2UI_getHandle(LV2UI* handle) 
{
	return (LV2UIQt5 *)(handle->handle);
}

void LV2UI_setPortData(void* const controller, uint32_t port_index, uint32_t buffer_size, uint32_t protocol, const void* buffer) 
{
	LV2Logger_log("LV2UI_setPortData\n");

	// TODO: check!!!! concurrency 
	LV2UI *handle = (LV2UI *) controller;
	LV2UIQt5 *uiHandle = LV2UI_getHandle(handle);
	if( uiHandle != NULL && uiHandle->instance != NULL ) {
		if ( protocol == 0 ) {
			LV2Instance_setControlPortValue(uiHandle->instance, port_index, *(float*)buffer);
			LV2UI_setUpdated(handle, true);
		}
	}
}

uint32_t LV2UI_getPortIndex(void* const controller, const char* symbol)
{
	LV2Logger_log("LV2UI_getPortIndex\n");

	LV2UI *handle = (LV2UI *) controller;
	LV2UIQt5 *uiHandle = LV2UI_getHandle(handle);

	LV2Int32 index = -1;
	LV2Plugin_getPortIndex(uiHandle->instance->plugin, &index, symbol);
	if( index != -1 ) {
		return index;
	}
	return LV2UI_INVALID_PORT_INDEX;
}

void LV2UI_malloc(LV2UI **handle, LV2Instance *instance)
{
	if( instance != NULL ) {
		(*handle) = (LV2UI *) malloc(sizeof(LV2UI));
		(*handle)->handle = (LV2UIQt5 *) malloc(sizeof(LV2UIQt5));

		LV2UIQt5* uiHandle = LV2UI_getHandle((*handle));
		uiHandle->instance = instance;
		uiHandle->suilHost = NULL;
		uiHandle->suilInstance = NULL;
		uiHandle->application = NULL;
		uiHandle->window = NULL;
		uiHandle->open = false;
		uiHandle->supported_ui = NULL;
		uiHandle->supported_ui_type = NULL;
		
		suil_init(NULL, NULL, SUIL_ARG_NONE);
		
		LilvUIs* uis = lilv_plugin_get_uis(uiHandle->instance->plugin->lilvPlugin);
		LilvNode* native_type = lilv_new_uri(uiHandle->instance->plugin->world->lilvWorld, NATIVE_UI_TYPE_URI);

		LILV_FOREACH (uis, u, uis) {
			const LilvUI* ui = lilv_uis_get(uis, u);
			if (lilv_ui_is_supported(ui, suil_ui_supported, native_type, &(uiHandle->supported_ui_type))) {
				uiHandle->supported_ui = ui;
			}
		}
		
		lilv_node_free(native_type);
	}
}

void LV2UI_free(LV2UI **handle)
{
	if( (*handle) != NULL) {
		LV2UIQt5 *uiHandle = LV2UI_getHandle((*handle));
		
		if( uiHandle->application != NULL ) {
			uiHandle->application->exit();
			uiHandle->application = NULL;
			uiHandle->window = NULL;
		}
		if( uiHandle->suilInstance != NULL ) {
			suil_instance_free(uiHandle->suilInstance);
			uiHandle->suilInstance = NULL;
		}
		if( uiHandle->suilHost != NULL ) {
			suil_host_free(uiHandle->suilHost);
			uiHandle->suilHost = NULL;
		}
		
		free ( uiHandle );
		free ( (*handle) );

		(*handle) = NULL;
	}
}

void LV2UI_isAvailable(LV2UI *handle, bool* available)
{
	(*available) = false;

	LV2UIQt5* uiHandle = LV2UI_getHandle(handle);
	if( uiHandle != NULL && uiHandle->supported_ui != NULL ){
		(*available) = true;
	}
}

void LV2UI_isOpen(LV2UI *handle, bool *open)
{
	LV2UIQt5* uiHandle = LV2UI_getHandle(handle);
	if( uiHandle != NULL && uiHandle->supported_ui != NULL ){
		(*open) = uiHandle->open;
	}
}

void LV2UI_open(LV2UI *handle) 
{
	LV2UIQt5* uiHandle = LV2UI_getHandle(handle);
	if( uiHandle != NULL && uiHandle->supported_ui != NULL ) {
		uiHandle->open = true;
	}
}

void LV2UI_close(LV2UI *handle)
{
	LV2UIQt5* uiHandle = LV2UI_getHandle(handle);
	if( uiHandle != NULL && uiHandle->open ) {
		uiHandle->open = false;
	}
}

void LV2UI_setControlPortValue(LV2UI *handle, LV2Int32 index, float value)
{
	LV2UIQt5* uiHandle = LV2UI_getHandle(handle);
	if( uiHandle != NULL && uiHandle->open && uiHandle->instance->plugin != NULL && uiHandle->suilInstance != NULL ) {
		if( index >= 0 && index < uiHandle->instance->plugin->portCount && uiHandle->instance->plugin->ports[index]->type == TYPE_CONTROL ) {
			suil_instance_port_event(uiHandle->suilInstance, index, sizeof(float), 0, &value);
		}
	}
}

void LV2UI_process(LV2UI *handle)
{
	LV2UIQt5* uiHandle = LV2UI_getHandle(handle);
	if( uiHandle != NULL ) {
		if( uiHandle->open ) {
			if( uiHandle->application == NULL ) {
				int args = 0;
				uiHandle->application = new QApplication(args, NULL, true);
				uiHandle->application->setQuitOnLastWindowClosed(true);

				char* bundle_path = lilv_file_uri_parse(lilv_node_as_uri(lilv_ui_get_bundle_uri(uiHandle->supported_ui)), NULL);
				char* binary_path = lilv_file_uri_parse(lilv_node_as_uri(lilv_ui_get_binary_uri(uiHandle->supported_ui)), NULL);

				uiHandle->suilHost = suil_host_new(LV2UI_setPortData, LV2UI_getPortIndex, NULL, NULL);

				uiHandle->suilInstance = suil_instance_new(
					uiHandle->suilHost,
					handle,
					NATIVE_UI_TYPE_URI,
					lilv_node_as_uri(lilv_plugin_get_uri(uiHandle->instance->plugin->lilvPlugin)),
					lilv_node_as_uri(lilv_ui_get_uri(uiHandle->supported_ui)),
					lilv_node_as_uri(uiHandle->supported_ui_type),
					bundle_path,
					binary_path,
					NULL);

				lilv_free(binary_path);
				lilv_free(bundle_path);

				QWidget* widget = static_cast<QWidget*>(suil_instance_get_widget(uiHandle->suilInstance));

				uiHandle->window = new LV2MainWindow(uiHandle);

				LilvNode* name = lilv_plugin_get_name(uiHandle->instance->plugin->lilvPlugin);
				uiHandle->window->setWindowTitle(lilv_node_as_string(name));
				
				lilv_node_free(name);
				uiHandle->window->setCentralWidget(widget);
			}
			if(!uiHandle->window->isVisible()) {
				uiHandle->window->show();
			}
		} else {
			if( uiHandle->window != NULL && uiHandle->window->isVisible()) {
				uiHandle->window->setVisible(false);
			}
		}
	} 
	if (uiHandle != NULL && uiHandle->application != NULL ) {
		uiHandle->application->processEvents();
	} else {
		usleep(100000);
	}
}
