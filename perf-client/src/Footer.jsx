import React from "react";
import './Footer.css';

export const Footer = (props) => {
    const {modes, mode, setMode, stopWs} = {...props};

    const stopClicked = () => {
        window.stop();
        stopWs();
        setMode('init');
    };

    return <div id='footer'>
        <div id='footer-text'>
            <div>{modes[mode]}</div>
        </div>
        <div id='stop-button' onClick={stopClicked}>Stop</div>
    </div>
};