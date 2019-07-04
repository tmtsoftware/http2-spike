import React, {Component} from "react";
import './Widget.css';

export class Widget extends Component {

    constructor(props) {
        super(props);
        this.state = {sse: undefined, connectionState: -1};
        this.interval = undefined
        this.stateMap = {
            '-1': 'INIT',
            '0': 'CONNECTING',
            '1': 'OPEN',
            '2': 'CLOSED'
        };

        this.modes = {
            http1: "http://localhost:9001/stream?durationInSeconds=3",
            http2: "https://localhost:9000/stream?durationInSeconds=5"
        };
    }

    componentDidMount() {
        const localSse = new EventSource(this.modes[this.props.mode]);
        localSse.onmessage = (e) => {
            // console.info(e.data)
            // console.info(e)
        };
        localSse.onerror = () => {
            console.log("EventSource failed.");
            localSse.close();
        };
        localSse.onopen = () => {
            console.log("Connection to server opened.");
        };
        this.setState({sse: localSse});

        this.interval = setInterval(() => {
            this.setState({connectionState: localSse.readyState})
        }, 1000);
    }

    componentWillUnmount() {
        clearInterval(this.interval)
    }


    render() {
        return <div className={`cell ${this.stateMap[this.state.connectionState].toLowerCase()}`} >
            <h2>
                {/*{this.props.index} - */}
                {this.stateMap[this.state.connectionState]}
            </h2>
        </div>
    }
}

Widget.defaultProps = {};

Widget.propTypes = {};