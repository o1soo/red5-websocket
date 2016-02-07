/*
 * RED5 Open Source Flash Server - https://github.com/red5
 * 
 * Copyright 2006-2015 by respective authors (see below). All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.red5.net.websocket;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.red5.net.websocket.listener.IWebSocketDataListener;
import org.red5.net.websocket.model.WSMessage;
import org.red5.server.api.scope.IScope;
import org.red5.server.plugin.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class WebSocketScope implements InitializingBean, DisposableBean {

    private Logger log = LoggerFactory.getLogger(WebSocketScope.class);

    private CopyOnWriteArraySet<WebSocketConnection> conns = new CopyOnWriteArraySet<WebSocketConnection>();

    private CopyOnWriteArraySet<IWebSocketDataListener> listeners = new CopyOnWriteArraySet<IWebSocketDataListener>();

    private IScope scope;

    private String path = "default";

    @Override
    public void afterPropertiesSet() throws Exception {
        register();
    }

    @Override
    public void destroy() throws Exception {
        unregister();
    }

    /**
     * Registers with the WebSocketScopeManager.
     */
    public void register() {
        log.info("Application scope: {}", scope);
        // set the logger to the app scope
        //log = Red5LoggerFactory.getLogger(WebSocketScope.class, scope.getName());
        WebSocketScopeManager manager = ((WebSocketPlugin) PluginRegistry.getPlugin("WebSocketPlugin")).getManager();
        manager.addWebSocketScope(this);
        log.info("WebSocket scope added");
        manager.addApplication(scope);
        log.info("WebSocket app added: {}", scope.getName());
    }

    /**
     * Un-registers from the WebSocketScopeManager.
     */
    public void unregister() {
        WebSocketScopeManager manager = ((WebSocketPlugin) PluginRegistry.getPlugin("WebSocketPlugin")).getManager();
        manager.removeWebSocketScope(this);
        log.info("WebSocket scope removed");
        manager.removeApplication(scope);
        log.info("WebSocket app removed: {}", scope.getName());
    }

    /**
     * Returns the set of connections.
     * 
     * @return conns
     */
    public Set<WebSocketConnection> getConns() {
        return conns;
    }

    /**
     * Returns the associated scope.
     * 
     * @return scope
     */
    public IScope getScope() {
        return scope;
    }

    /**
     * Sets the associated scope.
     * 
     * @param scope
     */
    public void setScope(IScope scope) {
        this.scope = scope;
    }

    /**
     * Sets the path.
     * 
     * @param path
     */
    public void setPath(String path) {
        this.path = path; // /room/name
    }

    /**
     * Returns the path of the scope.
     * 
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Add new connection on scope.
     * 
     * @param conn
     *            WebSocketConnection
     */
    public void addConnection(WebSocketConnection conn) {
        conns.add(conn);
        for (IWebSocketDataListener listener : listeners) {
            listener.onWSConnect(conn);
        }
    }

    /**
     * Remove connection from scope.
     * 
     * @param conn
     *            WebSocketConnection
     */
    public void removeConnection(WebSocketConnection conn) {
        conns.remove(conn);
        for (IWebSocketDataListener listener : listeners) {
            listener.onWSDisconnect(conn);
        }
    }

    /**
     * Add new listener on scope.
     * 
     * @param listener
     *            IWebSocketDataListener
     */
    public void addListener(IWebSocketDataListener listener) {
        log.info("addListener: {}", listener);
        listeners.add(listener);
    }

    /**
     * Remove listener from scope.
     * 
     * @param listener
     *            IWebSocketDataListener
     */
    public void removeListener(IWebSocketDataListener listener) {
        log.info("removeListener: {}", listener);
        listeners.remove(listener);
    }

    /**
     * Add new listeners on scope.
     * 
     * @param listenerList
     *            list of IWebSocketDataListener
     */
    public void setListeners(Collection<IWebSocketDataListener> listeners) {
        log.trace("setListeners: {}", listeners);
        this.listeners.addAll(listeners);
    }

    /**
     * Returns the listeners in an unmodifiable set.
     * 
     * @return listeners
     */
    public Set<IWebSocketDataListener> getListeners() {
        return Collections.unmodifiableSet(listeners);
    }

    /**
     * Check the scope state.
     * 
     * @return true:still have relation
     */
    public boolean isValid() {
        return (conns.size() + listeners.size()) > 0;
    }

    /**
     * Message received from client and passed on to the listeners.
     * 
     * @param message
     */
    public void onMessage(WSMessage message) {
        log.trace("Listeners: {}", listeners.size());
        for (IWebSocketDataListener listener : listeners) {
            try {
                listener.onWSMessage(message);
            } catch (Exception e) {
                log.warn("onMessage exception", e);
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WebSocketScope other = (WebSocketScope) obj;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WebSocketScope [path=" + path + ", listeners=" + listeners.size() + ", connections=" + conns.size() + "]";
    }

}
