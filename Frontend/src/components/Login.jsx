import React from "react";
import Button from "./Button";
import "./css/Login.css";

function Login() {
  function loginUser(){
    window.open("http://localhost:8081/saml/login/", "_self");
  };

  return (
    <section className="background_white">
      <div className="section_container">
        <div className="section_content">
          <div className="center_content mg_t_10">
            <img className="login_logo" alt="scp_logo" src={require("../images/Challenge-Overlay.png")}></img>
          </div>
          <div className="center_content mg_t_2">
            <Button color="orange" txt="Login" action={loginUser} />
          </div>
        </div>
      </div>
    </section>
  );
}

export default Login;
