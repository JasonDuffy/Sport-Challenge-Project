import PropTypes from "prop-types";
import "./NumberInput.css";

/**
 * @author Robin Hackh
 */

function NumberInput({ id, className, value, setValue, min, max, step }) {
  value = value < min ? min : value;
  value = value > max ? max : value;

  function handleChange(event) {
    let inputValue = Number(event.target.value);

    inputValue = inputValue < min ? min : inputValue;
    inputValue = inputValue > max ? max : inputValue;

    setValue(inputValue);
  }

  if(value != null && setValue != null){
    return <input id={id} className={"scp_number_input " + className} type="number" value={value} onChange={handleChange} step={step}></input>;
  }else{
    return <input id={id} className={"scp_number_input " + className} type="number" min={min} max={max} defaultValue={value} step={step}></input>;
  }
  
}

NumberInput.propTypes = {
  id: PropTypes.string,
  className: PropTypes.string,
  value: PropTypes.number,
  setValue: PropTypes.func,
  min: PropTypes.number,
  max: PropTypes.number,
  step: PropTypes.number,
};

NumberInput.defaultProps = {
  className: "",
  value: 1,
  min: 1,
  step: 1,
};

export default NumberInput;
