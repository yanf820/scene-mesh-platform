package com.scene.mesh.service.impl.cache;

import com.scene.mesh.foundation.api.cache.ICache;
import com.scene.mesh.model.session.TerminalSession;
import com.scene.mesh.service.api.cache.IDisposed;

public class TerminalSessionCache implements IDisposed {

    private ICache<String, TerminalSession> cache;

    public TerminalSessionCache(ICache<String, TerminalSession> cache) {
        this.cache = cache;
    }

    public TerminalSession findByTerminalId(String terminalId) {
        return cache.get(terminalId);
    }

    public void setTerminalSession(TerminalSession terminalSession) {
        this.cache.set(terminalSession.getTerminalId(), terminalSession);
    }

    public boolean deleteTerminalSession(String terminalId){
        return this.cache.delete(terminalId);
    }

    @Override
    public void dispose() {
        this.cache = null;
    }
}
