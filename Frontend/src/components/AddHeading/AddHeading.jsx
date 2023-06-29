import PropTypes from "prop-types";

/**
 * @author Robin Hackh
 */

function AddHeading({ action, entity, name }) {
  if (action === "add") {
    return (
      <div className="heading_underline_center mg_b_10">
        <span className="underline_center">{entity} hinzufügen</span>
      </div>
    );
  } else {
    return (
      <div className="heading_underline_center mg_b_10">
        <span className="underline_center">
          {entity} {name} bearbeiten
        </span>
      </div>
    );
  }
}

AddHeading.propTypes = {
  action: PropTypes.string.isRequired,
  entity: PropTypes.string.isRequired,
  name: PropTypes.string,
};

export default AddHeading;
