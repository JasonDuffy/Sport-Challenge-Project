import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import TextInput from "../../components/form/TextInput/TextInput";
import Button from "../../components/ui/button/Button";
import Checkbox from "../../components/form/Checkbox/Checkbox";
import ImageSelecter from "../../components/form/ImageSelecter/ImageSelecter";
import InfoMessage, { hideInfoMessage } from "../../components/form/InfoMessage/InfoMessage";
import { checkUserInput, fetchUserData, saveUser } from "./Profile";

function Profile() {
  const navigate = useNavigate();

  document.title = "Slash Challenge - Benutzerprofil";

  const [userID, setUserID] = useState(0);
  const [email, setEmail] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [userImage, setUserImage] = useState();
  const [userImageSource, setUserImageSource] = useState();
  const [userImageID, setUserImageID] = useState(0);
  const [communication, setCommunication] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function load() {
      let pageData;
      pageData = await fetchUserData();

      setUserID(pageData.memberResData.userID);
      setEmail(pageData.memberResData.email);
      setFirstName(pageData.memberResData.firstName);
      setLastName(pageData.memberResData.lastName);
      setCommunication(pageData.memberResData.communication);
      document.getElementById("user_communication_checkbox").checked = pageData.memberResData.communication;
      setUserImageID(pageData.memberResData.imageID);
      document.getElementById("headingText").innerText = "Willkommen zurück, " + pageData.memberResData.firstName + " " + pageData.memberResData.lastName;

      if(pageData.imageResData !== null){
        setUserImageSource("data:" + pageData.imageResData.type + ";base64, " + pageData.imageResData.data);
      } else {
        setUserImageSource(require("../../assets/images/Default-User.png"));
      }
    }

    load();
    document.getElementById("page_loading").style.display = "none";
    document.getElementById("page").style.display = "block";
  }, []);

  useEffect(() => {
    return () => {
      document.getElementById("page_loading").style.display = "flex";
      document.getElementById("page").style.display = "none";
    };
  }, []);

  async function submitHandle(event) {
    event.preventDefault();

    setLoading(true);
    hideInfoMessage();

    if (checkUserInput(firstName, lastName, userImage)) {
      let userObj = {};
      userObj.firstName = firstName;
      userObj.lastName = lastName;
      userObj.email = email;
      userObj.communication = document.getElementById("user_communication_checkbox").checked;
      
      let newUserData = await saveUser(userID, userObj, userImage, userImageID);

      if (newUserData !== null) {
        document.getElementById("headingText").innerText = "Willkommen zurück, " + newUserData.firstName + " " + newUserData.lastName;
        setCommunication(newUserData.communication);
      }

      if(newUserData.image.data !== "" && newUserData.image.data !== undefined){
        setUserImageSource("data:" + newUserData.image.type + ";base64, " + newUserData.image.data);
      }
    }
    setLoading(false);
  }

  return (
    <section className="background_white">
      <div className="section_container">
        <div className="section_content">
          <div className="heading_underline_center mg_b_10">
            <span className="underline_center" id="headingText">
              Willkommen zurück, Platzhalter
            </span>
          </div>
          <div className="form_container">
            <form onSubmit={submitHandle}>
              <div className="centered pd_1">
                <div className="center_content">
                  <img src={userImageSource} alt="User Image" className="round_image"></img>
                </div>
              </div>
              <InfoMessage />
              <div className="form_input_container pd_1">
                <h2>Wähle ein Profilbild aus.</h2>
                <span className="form_input_description">Das Bild sollte quadratisch sein.</span>
                <br />
                <ImageSelecter className="mg_t_2" value={userImage} setValue={setUserImage} alt="Profilbild" source={userImageSource} />
              </div>
              <div className="form_input_container pd_1 mg_t_1">
                <h2>Vorname</h2>
                <TextInput className="mg_t_2" value={firstName} setValue={setFirstName} maxLength={25} placeholder="Vorname" />
              </div>
              <div className="form_input_container pd_1 mg_t_1">
                <h2>Nachname</h2>
                <TextInput className="mg_t_2" value={lastName} setValue={setLastName} maxLength={25} placeholder="Vorname" />
              </div>
              <div className="form_input_container pd_1 mg_t_1">
                <label className="checkbox">
                  <Checkbox id="user_communication_checkbox" checked={communication} slider={true} />
                  <a className="mg_l_1">Ich möchte bei neuen Challenges, Boni und Inaktivität per E-Mail kontaktiert werden.</a>
                </label>
              </div>
              <div className="center_content mg_t_2">
                <Button color="orange" txt="Änderungen speichern" type="submit" loading={loading} />
              </div>
            </form>
          </div>
        </div>
      </div>
    </section>
  );
}

export default Profile;
