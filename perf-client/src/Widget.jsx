import React, {Component} from "react";
import './Widget.css';
import Gauge from 'react-svg-gauge';

export class Widget extends Component {

    constructor(props) {
        super(props);
        this.state = {sse: undefined, connectionState: -1, currentValue: -1};
        this.interval = undefined;
        this.stateMap = {
            '-1': 'INIT',
            '0': 'CONNECTING',
            '1': 'OPEN',
            '2': 'CLOSED'
        };

        this.isOpen = this.isOpen.bind(this);
    }

    componentDidMount() {
        const localSse = new EventSource(this.props.modes[this.props.mode]);
        localSse.onmessage = (e) => {
            this.setState({currentValue: e.data})
            // console.info(e)
        };
        localSse.onerror = () => {
            console.log("EventSource failed");
            localSse.close();
        };
        localSse.onopen = () => {
            // console.log("Connection to server opened.");
        };
        this.setState({sse: localSse});

        this.interval = setInterval(() => {
            this.setState({connectionState: localSse.readyState})
        }, 1000);
    }

    componentWillUnmount() {
        clearInterval(this.interval)
    }


    isOpen() {
        return (this.state.connectionState === 1)
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

Widget.defaultProps = {};

Widget.propTypes = {};