import React from "react";
import './ControlPanel.css';

export const ControlPanel = (props) => {
    const {setMode, mode} = {...props};

    const handleClick = (m) => {
        window.stop();
        setMode(m);
    };

    return <div id='control-panel'>
        <div onClick={() => handleClick('akka_http1_sse')} id='http1'
             className={mode === 'akka_http1_sse' ? 'active' : ''}>
            SSE with HTTP 1
        </div>
        <div onClick={() => handleClick('akka_http2_sse')} id='http2'
             className={mode === 'akka_http2_sse' ? 'active' : ''}>
            SSE with HTTP 2
        </div>
        <div onClick={() => handleClick('nginx_http2_sse')} id='http2'
             className={mode === 'nginx_http2_sse' ? 'active' : ''}>
            SSE with HTTP 2 (via Proxy)
        </div>
        <div onClick={() => handleClick('ws')} id='ws' className={mode === 'ws' ? 'active' : ''}>
            WebSockets
        </div>
    </div>
};