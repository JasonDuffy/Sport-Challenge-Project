import React from "react";
import PropTypes from "prop-types";
import "./Button.css";

/**
 *
 * @param color Possible value ( white | orange | blue ) [Required]
 * @param text  String inside the Button [Required]
 * @param action Function which runs onClick
 * @param type Button type
 * @param loading Show loading animation
 */

function Button(props) {
  let loadingAnimation;
  let buttonClass;
  if (props.loading === true) {
    loadingAnimation = (
      <div className="button_loading_ring is_loading">
        <div></div>
        <div></div>
        <div></div>
        <div></div>
      </div>
    );
    buttonClass = "button_" + props.color + " scp_button disabled";
  } else {
    loadingAnimation = null;
    buttonClass = "button_" + props.color + " scp_button";
  }

  return (
    <button className={buttonClass} type={props.type} onClick={props.action}>
      <span>{props.txt}</span>
      {loadingAnimation}
    </button>
  );
}

Button.propTypes = {
  color: PropTypes.string.isRequired,
  txt: PropTypes.string.isRequired,
};

export default Button;
