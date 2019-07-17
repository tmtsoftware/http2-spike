import React from "react";
import './ControlPanel.css';

export const ControlPanel = (props) => {
    const {setMode, mode} = {...props};

    const handleClick = (m) => {
        window.stop();
        setMode(m);
    };

    return <div id='control-panel'>
        <div onClick={() => handleClick('http1')} id='http1' className={mode === 'http1' ? 'active' : ''}>
            SSE with HTTP 1
        </div>
        <div onClick={() => handleClick('http2')} id='http2' className={mode === 'http2' ? 'active' : ''}>
            SSE with HTTP 2
        </div>
        <div onClick={() => handleClick('ws')} id='ws' className={mode === 'ws' ? 'active' : ''}>
            WebSockets
        </div>
    </div>
};