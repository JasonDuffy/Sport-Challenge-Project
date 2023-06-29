import PropTypes from "prop-types";
import "./TextareaInput.css";

/**
 * @author Robin Hackh
 */

function TextareaInput({ id, className, value, setValue, placeholder, maxLength, width, height }) {
  function handleChange(event) {
    setValue(event.target.value);
  }

  return (
    <div className={"scp_textarea_content " + className} style={{ width: width, height: height }}>
      <textarea id={id} className="scp_textarea_input" value={value} onChange={handleChange} placeholder={placeholder} maxLength={maxLength}></textarea>
      <div className="scp_textarea_char_counter">{value.length + " / " + maxLength}</div>
    </div>
  );
}

TextareaInput.propTypes = {
  id: PropTypes.string,
  className: PropTypes.string,
  value: PropTypes.string.isRequired,
  setValue: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  maxLength: PropTypes.number,
  height: PropTypes.string,
  width: PropTypes.string,
};

TextareaInput.defaultProps = {
  className: "",
  maxLength: 2048,
  height: "150px",
  width: "100%",
};

export default TextareaInput;
