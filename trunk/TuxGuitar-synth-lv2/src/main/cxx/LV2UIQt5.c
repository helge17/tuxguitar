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
#include "LV2Feature.h"
#include "LV2Instance.h"
#include "LV2Lock.h"
#include "LV2Logger.h"

#define NATIVE_UI_TYPE_URI LV2_UI__Qt5UI

struct LV2UIImpl {
	LV2Lock* lock;
	LV2Feature* feature;
	LV2Instance* instance;
	SuilHost* suilHost;
	SuilInstance* suilInstance;
	const LilvUI*   supported_ui;
	const LilvNode* supported_ui_type;
	bool open;
	bool updated;
	bool requestFocus;
	bool ignoreEvents;
	bool shouldRefresh;
	float refreshRate;
	uint32_t frameDelta;

	QMainWindow* window;
	QApplication *application;
};

class LV2MainWindow : public QMainWindow {
	public:
		LV2MainWindow(LV2UI* handle) {
			LV2MainWindow::handle = handle;
		}
	private:
		LV2UI* handle;
	protected:
		void closeEvent(QCloseEvent *event) {
			event->ignore();

			QMainWindow::setVisible(false);

			handle->open = false;
		}
};

void LV2UI_setPortData(void* const controller, uint32_t port_index, uint32_t buffer_size, uint32_t protocol, const void* buffer) 
{
	LV2Logger_log("LV2UI_setPortData buffer_size %d\n", buffer_size);

	LV2UI *handle = (LV2UI *) controller;
	if( handle != NULL && handle->instance != NULL && !handle->ignoreEvents) {
		LV2_URID eventTransferURID = LV2Feature_map(handle->feature, LV2_ATOM__eventTransfer);

		if ( protocol == 0 ) {

			if( LV2Lock_trylock(handle->lock) ) {
				LV2Instance_setControlPortValue(handle->instance, port_index, *(float*)buffer);
				LV2UI_setUpdated(handle, true);
				LV2Lock_unlock(handle->lock);
			}
		} else if ( protocol == eventTransferURID ) {
			if( LV2Lock_lock(handle->lock) ) {
				LV2_Atom_Sequence *seq = NULL;
				LV2Instance_getWorkBuffer(handle->instance, port_index, (void **)&seq);

				if( seq->atom.size == 0 ) {
					lv2_atom_sequence_clear(seq);
				}
				
				LV2_Atom_Event* event = lv2_atom_sequence_end(&seq->body, seq->atom.size);
				memcpy((&event->body), buffer, buffer_size);
				event->time.frames = 0;
				seq->atom.size += lv2_atom_pad_size(sizeof(LV2_Atom_Event) + event->body.size);
				
				LV2UI_setUpdated(handle, true);
				LV2Lock_unlock(handle->lock);
			}
		}
	}
}

uint32_t LV2UI_getPortIndex(void* const controller, const char* symbol)
{
	LV2UI *handle = (LV2UI *) controller;

	LV2Int32 index = -1;
	LV2Plugin_getPortIndex(handle->instance->plugin, &index, symbol);
	if( index != -1 ) {
		return index;
	}
	return LV2UI_INVALID_PORT_INDEX;
}

void LV2UI_malloc(LV2UI **handle, LV2Feature *feature, LV2Instance *instance, LV2Lock* lock)
{
	if( instance != NULL ) {
		(*handle) = (LV2UI *) malloc(sizeof(LV2UI));

		(*handle)->lock = lock;
		(*handle)->feature = feature;
		(*handle)->instance = instance;
		(*handle)->suilHost = NULL;
		(*handle)->suilInstance = NULL;
		(*handle)->application = NULL;
		(*handle)->window = NULL;
		(*handle)->open = false;
		(*handle)->ignoreEvents = false;
		(*handle)->shouldRefresh = false;
		(*handle)->supported_ui = NULL;
		(*handle)->supported_ui_type = NULL;
		(*handle)->frameDelta = 0;
		(*handle)->refreshRate = 0;
		
		suil_init(NULL, NULL, SUIL_ARG_NONE);
		
		LilvUIs* uis = lilv_plugin_get_uis((*handle)->instance->plugin->lilvPlugin);
		LilvNode* native_type = lilv_new_uri((*handle)->instance->plugin->world->lilvWorld, NATIVE_UI_TYPE_URI);

		LILV_FOREACH (uis, u, uis) {
			const LilvUI* ui = lilv_uis_get(uis, u);
			if (lilv_ui_is_supported(ui, suil_ui_supported, native_type, &((*handle)->supported_ui_type))) {
				(*handle)->supported_ui = ui;
			}
		}
		
		lilv_node_free(native_type);
	}
}

void LV2UI_free(LV2UI **handle)
{
	if( (*handle) != NULL) {
		if( (*handle)->application != NULL ) {
			(*handle)->application->exit();
			(*handle)->application = NULL;
			(*handle)->window = NULL;
		}
		if( (*handle)->suilInstance != NULL ) {
			suil_instance_free((*handle)->suilInstance);
			(*handle)->suilInstance = NULL;
		}
		if( (*handle)->suilHost != NULL ) {
			suil_host_free((*handle)->suilHost);
			(*handle)->suilHost = NULL;
		}
		
		free ( (*handle) );

		(*handle) = NULL;
	}
}

void LV2UI_isResizable(LV2UI *handle, bool* resizable)
{
	(*resizable) = true;

	if( handle != NULL && handle->supported_ui != NULL ){
		const LilvNode* s   = lilv_ui_get_uri(handle->supported_ui);
		LilvNode*       p   = lilv_new_uri(handle->instance->plugin->world->lilvWorld, LV2_CORE__optionalFeature);
		LilvNode*       fs  = lilv_new_uri(handle->instance->plugin->world->lilvWorld, LV2_UI__fixedSize);
		LilvNode*       nrs = lilv_new_uri(handle->instance->plugin->world->lilvWorld, LV2_UI__noUserResize);

		LilvNodes* fs_matches = lilv_world_find_nodes(handle->instance->plugin->world->lilvWorld, s, p, fs);
		LilvNodes* nrs_matches = lilv_world_find_nodes(handle->instance->plugin->world->lilvWorld, s, p, nrs);

		(*resizable) = (!fs_matches && !nrs_matches);

		lilv_nodes_free(nrs_matches);
		lilv_nodes_free(fs_matches);
		lilv_node_free(nrs);
		lilv_node_free(fs);
		lilv_node_free(p);
	}
}

void LV2UI_isAvailable(LV2UI *handle, bool* available)
{
	(*available) = false;

	if( handle != NULL && handle->supported_ui != NULL ){
		(*available) = true;
	}
}

void LV2UI_isOpen(LV2UI *handle, bool *open)
{
	if( handle != NULL && handle->supported_ui != NULL ){
		(*open) = handle->open;
	}
}

void LV2UI_open(LV2UI *handle) 
{
	if( handle != NULL && handle->supported_ui != NULL ) {
		handle->open = true;
	}
}

void LV2UI_close(LV2UI *handle)
{
	if( handle != NULL && handle->open ) {
		handle->open = false;
	}
}

void LV2UI_focus(LV2UI *handle)
{
	if( handle != NULL && handle->open ) {
		handle->requestFocus = true;
	}
}

void LV2UI_isUpdated(LV2UI *handle, bool *updated)
{
	if( handle != NULL ){
		(*updated) = handle->updated;
	}
}

void LV2UI_setUpdated(LV2UI *handle, bool updated)
{
	if( handle != NULL ){
		handle->updated = updated;
	}
}

void LV2UI_setControlPortValue(LV2UI *handle, LV2Int32 index, float value)
{
	if( handle != NULL && handle->open && handle->instance->plugin != NULL && handle->suilInstance != NULL ) {
		if( index >= 0 && index < handle->instance->plugin->portCount && handle->instance->plugin->ports[index]->type == TYPE_CONTROL ) {
			handle->ignoreEvents = true;

			suil_instance_port_event(handle->suilInstance, index, sizeof(float), 0, &value);

			handle->ignoreEvents = false;
		}
	}
}

void LV2UI_processPortEvents(LV2UI *handle, LV2PortFlow flow)
{
	if( handle != NULL && handle->window != NULL && handle->window->isVisible() ) {
		if( handle->instance->plugin != NULL && handle->instance->plugin->ports != NULL && handle->suilInstance != NULL ) {
			for (uint32_t i = 0; i < handle->instance->plugin->portCount; i ++) {
				LV2Port* port = handle->instance->plugin->ports[i];
				
				if( port->type == TYPE_CONTROL ) {
					if((port->flow == flow || flow == FLOW_UNKNOWN)) {
						float currentValue = 0;
						LV2Instance_getControlPortValue(handle->instance, i, &currentValue);

						handle->ignoreEvents = true;
						suil_instance_port_event(handle->suilInstance, i, sizeof(float), 0, &currentValue);
						handle->ignoreEvents = false;
					}
				}
				else if( port->type == TYPE_EVENT && port->flow == FLOW_OUT ) {
					if( LV2Lock_trylock(handle->lock) ) {

						LV2_URID eventTransferURID = LV2Feature_map(handle->feature, LV2_ATOM__eventTransfer);
						LV2_Atom_Sequence *sequence = NULL;
						LV2Instance_getWorkBuffer(handle->instance, i, (void **)&sequence);
						LV2_ATOM_SEQUENCE_FOREACH((LV2_Atom_Sequence *)sequence, ev) {
							if( ev->body.size > 0 ) {
								suil_instance_port_event(handle->suilInstance, i, sizeof(LV2_Atom) + ev->body.size, eventTransferURID, (const void*) &ev->body);
							}
						}
						sequence->atom.size = 0;

						LV2Lock_unlock(handle->lock);
					}
				}
			}
		}
	}
}

void LV2UI_processAudio(LV2UI *handle)
{
	if( handle != NULL ) {
		if( handle->window != NULL && handle->window->isVisible() ) {
			if( handle->instance->plugin != NULL && handle->instance->plugin->ports != NULL && handle->suilInstance != NULL ) {
				handle->frameDelta += handle->instance->config->bufferSize;
				if( handle->frameDelta > (handle->instance->config->sampleRate / handle->refreshRate) ) {
					handle->frameDelta = 0;
					handle->shouldRefresh = true;
				}
			}
		} else {
			// Otherwise clear the buffer.
			LV2_PLUGIN_PORT_BUFFER_FOREACH(handle->instance, TYPE_EVENT, FLOW_OUT, workBuffer, connectionBuffer, {
				LV2_ATOM_SEQUENCE_FOREACH((LV2_Atom_Sequence *) connectionBuffer, ev) {
					((LV2_Atom_Sequence *) workBuffer)->atom.size = 0;
				}
			})
		}
	}
}

void LV2UI_process(LV2UI *handle)
{
	if( handle != NULL ) {
		if( handle->open ) {
			if( handle->application == NULL ) {
				int args = 0;
				LilvNode* pluginName = lilv_plugin_get_name(handle->instance->plugin->lilvPlugin);
				char* bundlePath = lilv_file_uri_parse(lilv_node_as_uri(lilv_ui_get_bundle_uri(handle->supported_ui)), NULL);
				char* binaryPath = lilv_file_uri_parse(lilv_node_as_uri(lilv_ui_get_binary_uri(handle->supported_ui)), NULL);

				handle->application = new QApplication(args, NULL, true);
				handle->application->setQuitOnLastWindowClosed(true);
				
				handle->suilHost = suil_host_new(LV2UI_setPortData, LV2UI_getPortIndex, NULL, NULL);
				
				if( handle->suilHost != NULL ) {
					handle->window = new LV2MainWindow(handle);
					LV2Feature_getFeature(handle->feature, LV2_UI__parent)->data = handle->window;

					handle->suilInstance = suil_instance_new(
						handle->suilHost,
						handle,
						NATIVE_UI_TYPE_URI,
						lilv_node_as_uri(lilv_plugin_get_uri(handle->instance->plugin->lilvPlugin)),
						lilv_node_as_uri(lilv_ui_get_uri(handle->supported_ui)),
						lilv_node_as_uri(handle->supported_ui_type),
						bundlePath,
						binaryPath,
						LV2Feature_getFeatures(handle->feature));
				}

				if( handle->suilInstance != NULL ) {
					handle->window->setWindowIcon(QIcon("./tuxguitar-synth-lv2.png"));
					handle->window->setWindowTitle(lilv_node_as_string(pluginName));
					handle->window->setCentralWidget(static_cast<QWidget*>(suil_instance_get_widget(handle->suilInstance)));
					handle->refreshRate = MIN(60, (float) QGuiApplication::primaryScreen()->refreshRate());
				}
				else {
					handle->application->exit();
					handle->application = NULL;
					handle->window = NULL;
					handle->supported_ui = NULL;
					handle->open = false;
					return;
				}
				
				lilv_node_free(pluginName);
				lilv_free(binaryPath);
				lilv_free(bundlePath);
			}
			if(!handle->window->isVisible()) {
				QWidget* widget = handle->window->centralWidget();
				widget->show();
				widget->setMinimumSize(widget->width(), widget->height());
				handle->window->adjustSize();
				
				bool resizable = false;
				LV2UI_isResizable(handle, &resizable);
				if(!resizable ){
					handle->window->setFixedSize(handle->window->width(), handle->window->height());
				}
				handle->window->show();
				
				QPoint screenCenter = QGuiApplication::primaryScreen()->geometry().center();
				QSize windowSize = handle->window->size();
				if( windowSize.width() > 0 && windowSize.height() > 0 ) {
					handle->window->move(screenCenter.x() - (windowSize.width() / 2), screenCenter.y() - (windowSize.height() / 2));
				}
				
				LV2UI_processPortEvents(handle, FLOW_UNKNOWN);
			}
			if( handle->shouldRefresh ) {
				handle->shouldRefresh = false;

				LV2UI_processPortEvents(handle, FLOW_OUT);
			}
			if( handle->requestFocus ) {
				handle->requestFocus = false;

				handle->window->raise();
				handle->window->activateWindow();
			}
		} else {
			if( handle->window != NULL && handle->window->isVisible()) {
				handle->window->setVisible(false);
			}
		}
	} 
	if (handle != NULL && handle->application != NULL ) {
		handle->application->processEvents();
	} else {
		usleep(100000);
	}
}
