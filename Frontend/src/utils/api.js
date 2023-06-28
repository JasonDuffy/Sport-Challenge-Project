/**
 * @author Robin Hackh
 */

const serverUrlConfig = {
  serverProtocol: process.env.REACT_APP_SERVERPROTOCOL == null ? "http" : process.env.REACT_APP_SERVERPROTOCOL, // Set to used protocol with backend server
  serverPort: process.env.REACT_APP_SERVERPORT == null ? 8081 : process.env.REACT_APP_SERVERPORT, // Set to port number of backend server
  serverName: process.env.REACT_APP_SERVERNAME == null ? "localhost" : process.env.REACT_APP_SERVERNAME, // Set to hostname of backend server
};

async function apiFetch(url, method, headers, data) {
  const serverURL = serverUrlConfig.serverProtocol + "://" + serverUrlConfig.serverName + ":" + serverUrlConfig.serverPort;
  let error = false;
  let status;
  let response;
  let resData;

  response = await fetch(serverURL + url, { method: method, headers: headers, body: data, credentials: "include" });
  status = response.status;

  if (!response.ok) {
    error = true;
    return { error, status };
  }

  if(method !== "DELETE"){
    resData = await response.json();
  }

  return { error, status, resData };
}

export default apiFetch;
