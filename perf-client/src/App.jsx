import * as React from 'react';
import {useState} from 'react';
import './App.css';
import {Dashboard} from "./Dashboard";
import {ControlPanel} from "./ControlPanel";
import {Footer} from "./Footer";

function App() {

    const modes = {
        http1: "http://another-host:8080/sse?durationInSeconds=7",
        http2: "https://another-host:9090/sse",
        ws: "ws://localhost:8080/websocket"
    };

    const [mode, setMode] = useState('init');

    return (
        <div id={'layout'} className={(mode === 'init' ? '' : ' active')}>

            <ControlPanel setMode={setMode} mode={mode}/>

            {mode === 'init' ? null : <Dashboard modes={modes} mode={mode}/>}

            {mode === 'init' ? null : <Footer modes={modes} mode={mode} setMode={setMode}/>}

        </div>
    );
}

export default App;
