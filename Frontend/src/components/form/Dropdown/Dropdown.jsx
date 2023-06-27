import PropTypes from "prop-types";
import "./Dropdown.css";

/**
 * @author Robin Hackh
 */

function Dropdown({ id, className, value, setValue, defaultOption, options }) {
  function handleChange(event) {
    setValue(event.target.value);
  }

  return (
    <select id={id} className={"scp_dropdown " + className} value={value} onChange={handleChange}>
      <option value={0} disabled>
        {defaultOption}
      </option>
      {options.map((item) => (
        <option key={item.id} value={item.id}>
          {item.name}
        </option>
      ))}
    </select>
  );
}

Dropdown.propTypes = {
  id: PropTypes.string,
  className: PropTypes.string,
  value: PropTypes.string.isRequired,
  setValue: PropTypes.func.isRequired,
  defaultOption: PropTypes.string.isRequired,
  options: PropTypes.array.isRequired,
};

Dropdown.defaultProps = {
  className: "",
  value: "0",
  options: [],
};

export default Dropdown;
