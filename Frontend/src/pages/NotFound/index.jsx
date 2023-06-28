import { useEffect } from "react";
import "./NotFound.css";

/**
 * @author Robin Hackh
 */
function NotFound() {
  useEffect(() => {
    document.getElementById("page_loading").style.display = "none";
    document.getElementById("page").style.display = "block";
  }, []);

  useEffect(() => {
    return () => {
      document.getElementById("page_loading").style.display = "flex";
      document.getElementById("page").style.display = "none";
    };
  }, []);

  return (
    <>
      <div className="error_background_image"></div>
      <div className="error_container">
        <div className="error_content">
          <div className="error_text_container">
            <span>
              Houston,
              <br />
              wir haben ein Problem.
            </span>
            <span>404</span>
            <span>Seite nicht gefunden</span>
            <a href="/">
              <div className="error_home_button">Zur Startseite</div>
            </a>
          </div>
          <div className="error_image_container mg_l_3">
            <img className="error_image" src={require("../../assets/images/Astronaut_Error_Page_clear.png")} alt="Astronaut mit Laptop sitzt auf Mond"></img>
          </div>
        </div>
      </div>
      <div className="error_image_credits">
        <a href="https://de.freepik.com/vektoren-kostenlos/niedlich-astronaut-halten-tablette-auf-mond-karikatur-vektor-symbol-abbildung-wissenschaft-technologie-isolated_39322493.htm#query=astronaut%20laptop&position=21&from_view=author#position=21&query=astronaut%20laptop">
          Astronaut von catalyststuff
        </a>
        auf Freepik
        <a className="mg_l_2" href="https://de.freepik.com/vektoren-kostenlos/szene-mit-bergen-am-nachthintergrund_4882552.htm#query=Sternenhimmel%20cartoon&position=22&from_view=search&track=ais">
          Hintergrund von brgfx
        </a>
        auf Freepik
      </div>
    </>
  );
}

export default NotFound;
