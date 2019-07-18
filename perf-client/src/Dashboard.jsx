import React from "react";
import {WsWidget} from "./WsWidget";
import {Widget} from "./Widget";

export const Dashboard = (props) => {
    const {modes, mode, addWsConnection} = {...props};

    const boxes = [];

    for (let i = 1; i < 49; i++) {
        boxes.push(mode === 'ws' ?
            <WsWidget addWsConnection={addWsConnection} modes={modes} mode={mode} key={mode + i.toString()}
                      index={mode + i.toString()}/> :
            <Widget modes={modes} mode={mode} key={mode + i.toString()} index={mode + i.toString()}/>)
    }

    return mode === 'init' ? null : <div id='dashboard'>
        {boxes}
    </div>
};