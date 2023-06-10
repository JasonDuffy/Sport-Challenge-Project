import React from "react";
import Button from "./Button";
import { useState, useEffect } from "react";
import GlobalVariables from "../GlobalVariables.js"

const userAction = async () => {
  const res = await fetch(GlobalVariables.serverURL + "/saml/user/", { method: "GET", credentials: "include" });
  console.log(res);
  const data = await res.json();
  console.log(data);
};
const userAction2 = () => {
  window.open(GlobalVariables.serverURL + "/saml/login/", "_self");
};
const userAction3 = async () => {
  const res = await fetch(GlobalVariables.serverURL + "/member/0", { method: "GET", credentials: "include" });
  console.log(res);
  const data = await res.json();
  console.log(data);
};

function Test() {
  const [image, setimage] = useState("");
  const fileinput = document.getElementById("file");

  async function testUpload(event) {
    event.preventDefault();
    
    var formData = new FormData();
    const file = fileinput.files[0];
    formData.append("file", file);
    const res = await fetch(GlobalVariables.serverURL + "/image/upload/", { method: "POST", body: formData, credentials: "include" });
    console.log(res);
  };

  async function loadImg(){
    const imageDisplay = document.getElementById("image_display");
    const res = await fetch(GlobalVariables.serverURL + "/image/1", { method: "GET", credentials: "include" });
    console.log(res);
    const data = await res.json();
    console.log(data);

    imageDisplay.setAttribute("src", "data:" + data.type + ";base64, " + data.data);
  };

  return (
    <div className="mg_t_8">
      <Button color="orange" txt="samlAPI" action={userAction} />
      <Button color="orange" txt="restAPI" action={userAction3} />
      <Button color="orange" txt="Login" action={userAction2} />
      <Button color="orange" txt="LoadImg" action={loadImg} />
      <form onSubmit={testUpload}>
        <input
          id="file"
          type="file"
          value={image}
          name="image"
          onChange={(event) => {
            setimage(event.target.value);
          }}
        ></input>
        <input type="submit" value="Submit"></input>
      </form>
      <img height="500" id="image_display"></img>
    </div>
  );
}

export default Test;