import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { checkBonusInput, fetchBonusChallengeData, fetchBonusData, fetchFormData, fetchSportTable, saveOrUpdateBonus } from "./AddBonus.js";
import "./AddBonus.css";
import "../../assets/css/form.css";
import AddHeading from "../../components/AddHeading/AddHeading";
import InfoMessage, { hideInfoMessage } from "../../components/form/InfoMessage/InfoMessage";
import TextInput from "../../components/form/TextInput/TextInput";
import TextareaInput from "../../components/form/TextareaInput/TextareaInput";
import Dropdown from "../../components/form/Dropdown/Dropdown";
import NumberInput from "../../components/form/NumberInput/NumberInput";
import Checkbox from "../../components/form/Checkbox/Checkbox";
import Datepicker, { converDateToString } from "../../components/form/Datepicker/Datepicker";
import Button from "../../components/ui/button/Button";

/**
 * @author Robin Hackh
 */

function AddBonus() {
  const action = useParams().action.toLocaleLowerCase();
  const navigate = useNavigate();
  const location = useLocation();

  const [bonusHeadingName, setBonusHeadingName] = useState("");
  const [bonusName, setBonusName] = useState("");
  const [bonusDescription, setBonusDescription] = useState("");
  const [bonusFactor, setBonusFactor] = useState(1);
  const [bonusStartDate, setBonusStartDate] = useState(converDateToString(new Date()));
  const [bonusEndDate, setBonusEndDate] = useState(converDateToString(new Date()));
  const [challengeID, setchallengeID] = useState("0");
  const [challengeDropdownData, setChallengeDropdownData] = useState([]);
  const [sportTableData, setSportTableData] = useState([]);
  const [loading, setLoading] = useState(false);

  //Load Component
  useEffect(() => {
    async function load() {
      let pageData;

      if (action === "edit") {
        pageData = await fetchBonusChallengeData(location.state.id);
        setchallengeID(pageData.id.toString());

        let challengeIDBuffer = pageData.id;

        pageData = await fetchBonusData(location.state.id);
        setBonusHeadingName(pageData.name);
        setBonusName(pageData.name);
        setBonusDescription(pageData.description);
        setBonusFactor(pageData.factor);
        setBonusStartDate(pageData.startDate);
        setBonusEndDate(pageData.endDate);

        document.title = "Slash Challenge - " + pageData.name + " bearbeiten";

        pageData = await fetchFormData(challengeIDBuffer);
      } else {
        document.title = "Slash Challenge - Neuen Bonus erstellen";

        pageData = await fetchFormData(0);
      }

      setChallengeDropdownData(pageData.challengeDropdownResData);
    }

    load();
    document.getElementById("page_loading").style.display = "none";
    document.getElementById("page").style.display = "block";
  }, [action]);

  //Component unmount
  useEffect(() => {
    return () => {
      document.getElementById("page_loading").style.display = "flex";
      document.getElementById("page").style.display = "none";
    }
}, [])

  //If the challegneID changes update the sportTable
  useEffect(() => {
    async function reloadTable() {
      const sportTableResData = await fetchSportTable(location.state.id, challengeID);
      setSportTableData(sportTableResData);
    }

    reloadTable();
  }, [challengeID]);

  /**
   * Form Submit
   */
  async function submitHandle(event) {
    event.preventDefault();

    setLoading(true);
    hideInfoMessage();

    let challengeSportIDs = [];
    let bonusObj = {};
    const tableCells = document.getElementsByClassName("sport_table_checkbox_cell");

    //get all checked ChallengeSportIDs
    for (const tableCell of tableCells) {
      if (tableCell.getElementsByTagName("input")[0].checked === true) {
        challengeSportIDs.push(tableCell.dataset.challengeSportId);
      }
    }

    if (checkBonusInput(bonusName, bonusDescription, challengeID, bonusFactor, challengeSportIDs.length, bonusStartDate, bonusEndDate)) {
      bonusObj.startDate = bonusStartDate;
      bonusObj.endDate = bonusEndDate;
      bonusObj.factor = bonusFactor;
      bonusObj.name = bonusName;
      bonusObj.description = bonusDescription;

      await saveOrUpdateBonus(location.state.id, bonusObj, challengeSportIDs);
    }

    setLoading(false);
  }

  return (
    <section className="background_white">
      <div className="section_container">
        <div className="section_content">
          <AddHeading action={action} entity="Bonus" name={bonusHeadingName} />
          <InfoMessage />
          <div className="form_container">
            <form onSubmit={submitHandle}>
              <div className="form_input_container pd_1">
                <h2>Gebe dem Bonus einen Namen</h2>
                <TextInput className="mg_t_2" value={bonusName} setValue={setBonusName} maxLength={32} placeholder="Bonus Name" />
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Was ist der Anlass des Bonuses?</h2>
                <TextareaInput className="mg_t_2" maxLength={128} value={bonusDescription} setValue={setBonusDescription} placeholder="Beschreibe den Bonus" />
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Wähle eine Challenge für den Bonus aus</h2>
                <Dropdown className="mg_t_2" value={challengeID} setValue={setchallengeID} defaultOption="Challenge wählen" options={challengeDropdownData} />
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Welchen Faktor soll der Bonus haben?</h2>
                <NumberInput className="mg_t_2" value={bonusFactor} setValue={setBonusFactor} min={0.1} step={0.1} />
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Wähle die Sportarten aus, die von dem Bonus beeinflusst werden</h2>
                <table className="form_sport_table mg_t_2">
                  <thead>
                    <tr>
                      <th>Sportart</th>
                      <th>Auswahl</th>
                    </tr>
                  </thead>
                  <tbody>
                    {sportTableData.map((item) => (
                      <tr key={item.id}>
                        <td>{item.name}</td>
                        <td className="sport_table_checkbox_cell" data-challenge-sport-id={item.id}>
                          <Checkbox checked={item.checked} slider={true} />
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              <div className="form_input_container pd_1 mg_t_2">
                <h2>Wähle das Start- und Enddatum für den Bonus</h2>
                <div className="form_input_date_container mg_t_2">
                  <Datepicker startDateValue={bonusStartDate} setStartDate={setBonusStartDate} endDateValue={bonusEndDate} setEndDate={setBonusEndDate} />
                </div>
              </div>
              <div className="center_content mg_t_2">
                {action === "edit" && <Button color="orange" txt="Bonus bearbeiten" type="submit" loading={loading} />}
                {action === "add" && <Button color="orange" txt="Bonus hinzufügen" type="submit" loading={loading} />}
              </div>
            </form>
          </div>
        </div>
      </div>
    </section>
  );
}

export default AddBonus;
