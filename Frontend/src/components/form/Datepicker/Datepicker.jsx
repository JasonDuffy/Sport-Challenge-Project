import PropTypes from "prop-types";
import "./Datepicker.css";

/**
 * @author Robin Hackh
 */



function checkDateisValid(date) {
  return date instanceof Date && !isNaN(date);
}

export function converDateToString(date){
  const dateOptions = { day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit" };
  return date.toLocaleDateString("de-GE", dateOptions).replace(" ", "");
}

//Converts a string to date string format: Day.Month.Year,Hour:Minute
function convertStringToDate(dateString) {
  const date = dateString.split(",")[0];
  const time = dateString.split(",")[1];

  const dateTimeObj = {
    day: Number(date.split(".")[0]),
    month: Number(date.split(".")[1]) - 1,
    year: Number(date.split(".")[2]),
    hour: Number(time.split(":")[0]) + 2,
    minute: Number(time.split(":")[1]),
  };

  return new Date(dateTimeObj.year, dateTimeObj.month, dateTimeObj.day, dateTimeObj.hour, dateTimeObj.minute);
}

function Datepicker({ startDateId, endDateId, className, startDateValue, setStartDate, endDateValue, setEndDate }) {
  //If start date is after end date end start date to start date
  function handleStartDateChange(event) {
    if (checkDateisValid(new Date(event.target.value))) {
      if (new Date(event.target.value) > convertStringToDate(endDateValue)) {
        setEndDate(converDateToString(new Date(event.target.value)));
      }
      setStartDate(converDateToString(new Date(event.target.value)));
    }
  }

  //If end date is before start date set start date to end date
  function handleEndDateChange(event) {
    if (checkDateisValid(new Date(event.target.value))) {
      if (new Date(event.target.value) < convertStringToDate(startDateValue)) {
        setStartDate(converDateToString(new Date(event.target.value)));
      }
      setEndDate(converDateToString(new Date(event.target.value)));
    }
  }

  return (
    <div className="scp_datepicker_content">
      <div>
        <span className="scp_datepicker_mobile_description">Start</span>
        <input
          id={startDateId}
          className={"scp_datepicker " + className}
          type="datetime-local"
          value={convertStringToDate(startDateValue).toISOString().substring(0, 16)}
          onChange={handleStartDateChange}
        ></input>
      </div>
      <span className="scp_datepicker_separator">----</span>
      <div>
        <span className="scp_datepicker_mobile_description">Ende</span>
        <input
          id={endDateId}
          className={"scp_datepicker " + className}
          type="datetime-local"
          value={convertStringToDate(endDateValue).toISOString().substring(0, 16)}
          onChange={handleEndDateChange}
        ></input>
      </div>
    </div>
  );
}

Datepicker.propTypes = {
  startDateId: PropTypes.string,
  endDateId: PropTypes.string,
  className: PropTypes.string,
  startDateValue: PropTypes.string.isRequired,
  setStartDate: PropTypes.func.isRequired,
  endDateValue: PropTypes.string.isRequired,
  setEndDate: PropTypes.func.isRequired,
};

Datepicker.defaultProps = {
  className: "",
};

export default Datepicker;
