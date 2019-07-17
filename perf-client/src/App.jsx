import * as React from 'react';
import {useState} from 'react';
import './App.css';
import './ControlPanel.css';
import './Footer.css';
import {Widget} from "./Widget";
import {WsWidget} from "./WsWidget";

function App() {
    const boxes = [];

    const modes = {
        http1: "http://another-host:8080/sse?durationInSeconds=7",
        http2: "https://another-host:9090/sse",
        ws: "ws://localhost:8080/websocket"
    };

    const [mode, setMode] = useState('init');

    for (let i = 1; i < 49; i++) {
        boxes.push(mode === 'ws' ?
            <WsWidget modes={modes} mode={mode} key={mode + i.toString()} index={mode + i.toString()}/> :
            <Widget modes={modes} mode={mode} key={mode + i.toString()} index={mode + i.toString()}/>)
    }

    const handleClick = (m) => {
        window.stop();
        setMode(m);
    };

    return (
        <div id={'layout'} className={(mode === 'init' ? '' : ' active')}>
            <div id='control-panel'>
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
            {mode === 'init' ? null : <div id='dashboard'>
                {boxes}
            </div>
            }
            {mode === 'init' ? null : <div id='footer'>
                <div id='footer-text'>
                    <div>{modes[mode]}</div>
                </div>
                <div id='stop-button'>Stop</div>
            </div>
            }
        </div>
    );
}

export default App;
