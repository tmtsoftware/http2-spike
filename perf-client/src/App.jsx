import * as React from 'react';
import {useState} from 'react';
import './App.css';
import {Widget} from "./Widget";

function App() {
    const boxes = [];

    const modes = {
        http1: "http://another-host:8080/stream?durationInSeconds=7",
        http2: "https://another-host:9090/stream"
    };

    const [mode, setMode] = useState('init');

    for (let i = 1; i < 49; i++) {
        boxes.push(<Widget modes={modes} mode={mode} key={mode + i.toString()} index={mode + i.toString()}/>)
    }

    const handleClick = (m) => {
        window.stop();
        setMode(m);
    };

    return (
        <div>
            <div id='control-panel'>
                <div onClick={() => handleClick('http1')} id='http1' className={mode === 'http1' ? 'active' : ''}>
                    HTTP 1.1
                </div>
                <div onClick={() => handleClick('http2')} id='http2' className={mode === 'http2' ? 'active' : ''}>
                    HTTP 2
                </div>
            </div>
            <div id='dashboard'>
                {mode === 'init' ? null : boxes}
            </div>
            {mode === 'init' ? null : <div id='footer'>
                {modes[mode]}
            </div>}
        </div>
    );
}

export default App;
