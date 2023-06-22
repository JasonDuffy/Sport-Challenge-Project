import PropTypes from "prop-types";

/**
 * @author Robin Hackh
 */

function AddHeading({ action, entitie, name }) {
  if (action === "add") {
    return (
      <div className="heading_underline_center mg_b_10">
        <span className="underline_center">{entitie} hinzufügen</span>
      </div>
    );
  } else {
    return (
      <div className="heading_underline_center mg_b_10">
        <span className="underline_center">
          {entitie} {name} editieren
        </span>
      </div>
    );
  }
}

AddHeading.propTypes = {
  action: PropTypes.string.isRequired,
  entitie: PropTypes.string.isRequired,
  name: PropTypes.string,
};

export default AddHeading;
