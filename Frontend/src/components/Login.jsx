import React from "react";
import Button from "./Button";

const userAction = async () => {
    const response = await fetch('http://localhost:8081/saml/user');
}

function Login(){
    return <Button color="orange" txt="test" action={userAction}/>
}

export default Login;