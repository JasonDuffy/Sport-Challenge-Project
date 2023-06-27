import PropTypes from "prop-types";
import "./ImageSelecter.css";

/**
 * @author Robin Hackh
 */

function ImageSelecter({ id, className, value, setValue, source, alt }) {
  if (value != null) source = URL.createObjectURL(value);

  function handleChange(event) {
    setValue(event.target.files[0]);
  }

  return (
    <>
      <input id={id} className={"scp_image_input " + className} type="file" accept="image/*" onChange={handleChange}></input>
      {source != null && (
        <div className="preview_image_container mg_t_2">
          <img src={source} alt={alt} height={200} width={200}></img>
        </div>
      )}
    </>
  );
}

ImageSelecter.propTypes = {
  id: PropTypes.string,
  className: PropTypes.string,
  setValue: PropTypes.func.isRequired,
  alt: PropTypes.string.isRequired,
};

ImageSelecter.defaultProps = {
  className: "",
};

export default ImageSelecter;
