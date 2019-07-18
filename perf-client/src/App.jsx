import * as React from 'react';
import {useState} from 'react';
import './App.css';
import {Dashboard} from "./Dashboard";
import {ControlPanel} from "./ControlPanel";
import {Footer} from "./Footer";

function App() {

    const modes = {
        akka_http1_sse: "http://localhost:8080/sse?durationInSeconds=7",
        akka_http2_sse: "https://localhost:9090/sse",
        nginx_http2_sse: "https://localhost/sse",
        ws: "ws://localhost:8080/websocket"
    };

    const [mode, setMode] = useState('init');

    const connections = [];

    const addWsConnection = (ws) => connections.push(ws);

    const stopWs = () => {
        connections.map(x => x.close());
        console.info(connections);
    };

    return (
        <div id={'layout'} className={(mode === 'init' ? '' : ' active')}>

            <ControlPanel setMode={setMode} mode={mode}/>

            {mode === 'init' ? null : <Dashboard addWsConnection={addWsConnection} modes={modes} mode={mode}/>}

            {mode === 'init' ? null : <Footer modes={modes} mode={mode} setMode={setMode} stopWs={stopWs}/>}

        </div>
    );
}

export default App;
