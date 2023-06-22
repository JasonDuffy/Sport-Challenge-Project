import PropTypes from 'prop-types';
import "./TextInput.css";

/**
 * @author Robin Hackh
 */

function TextInput({ id, className, value, setValue, placeholder, maxLength }) {

  function handleChange(event) {
    setValue(event.target.value);
  }

  return (
    <input
      id={id}
      className={"scp_text_input " + className}
      type="text"
      value={value}
      onChange={handleChange}
      placeholder={placeholder}
      maxLength={maxLength}
    ></input>
  );
}

TextInput.propTypes = {
  id: PropTypes.string,
  className: PropTypes.string,
  value: PropTypes.string.isRequired,
  setValue: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  maxLength: PropTypes.number,
};

TextInput.defaultProps = {
  className: "",
};

export default TextInput;
