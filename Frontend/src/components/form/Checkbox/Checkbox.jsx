import PropTypes from "prop-types";
import "./Checkbox.css";

/**
 * @author Robin Hackh
 */

function Checkbox({ id, className, checked, slider }) {
  if (slider === true) {
    return (
      <label className={"scp_switch " + className}>
        <input id={id} className={"scp_switch_input"} type="checkbox" defaultChecked={checked}></input>
        <span className="scp_slider"></span>
      </label>
    );
  } else {
    return <input id={id} className={"scp_checkbox " + className} type="checkbox" defaultChecked={checked}></input>;
  }
}

Checkbox.propTypes = {
  id: PropTypes.string,
  className: PropTypes.string,
  selected: PropTypes.bool,
  slider: PropTypes.bool,
};

Checkbox.defaultProps = {
  className: "",
  selected: false,
  slider: false,
};

export default Checkbox;
