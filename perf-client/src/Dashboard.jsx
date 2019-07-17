import React from "react";
import PropTypes from "prop-types";

export const Dashboard = (props) => {
    const {children} = {...props};
    return <div>
        {children}
    </div>
};

Dashboard.defaultProps = {};

Dashboard.propTypes = {};