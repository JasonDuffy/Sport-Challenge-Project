import React from 'react';
import PropTypes from 'prop-types'; 
import './css/Button.css';

/**
 * 
 * @param color Possible value ( white | orange | blue ) [Required]
 * @param text  String inside the Button [Required]
 * @param action Function which runs onClick
 */

function Button(props){
    return (
        <button className={"button_" + props.color + " scp_button"} type="button" onClick={props.action}>{props.txt}</button>
    );
}

Button.propTypes = {
    color: PropTypes.string.isRequired,
    txt: PropTypes.string.isRequired
}

export default Button;