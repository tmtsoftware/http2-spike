import React, {Component} from "react";
import Gauge from 'react-svg-gauge';

export class WsWidget extends Component {

    constructor(props) {
        super(props);
        this.state = {connectionState: -1, currentValue: -1};
        this.stateMap = {
            '-1': 'INIT',
            '0': 'CONNECTING',
            '1': 'OPEN',
            '2': 'CLOSING',
            '3': 'CLOSED'
        };

        this.isOpen = this.isOpen.bind(this);
    }

    isOpen() {
        return (this.state.connectionState === 1)
    }

    componentDidMount() {
        this.setState({connectionState: 0});
        const webSocket = new WebSocket(this.props.modes[this.props.mode]);
        webSocket.onmessage = (m) => {
            this.setState({currentValue: parseInt(m.data)})
        };
        webSocket.onopen = () => this.setState({connectionState: webSocket.readyState});
        webSocket.onclose = () => this.setState({connectionState: webSocket.readyState});
        webSocket.onerror = () => this.setState({connectionState: webSocket.readyState});
        this.props.addWsConnection(webSocket);
    }

    render() {
        return <div className={`cell ${this.stateMap[this.state.connectionState].toLowerCase()}`}>
            {!this.isOpen() ?
                <h2>
                    {this.stateMap[this.state.connectionState]}
                </h2>
                : <div>
                    <Gauge value={this.state.currentValue} label={''} width={100} height={50}/>
                </div>
            }
        </div>
    }
}
