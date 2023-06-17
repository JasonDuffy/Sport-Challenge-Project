/**
 * This module allows changing of settings
 * @author Jason Patrick Duffy
 */
const GlobalVariables = {
    serverProtocol: "http", // Set to used protocol with backend server
    serverPort: 8081, // Set to port number of backend server
    serverName: "localhost", // Set to hostname of backend server
}

// Outside of declaration as it would not be possible to access otherwise
GlobalVariables.serverURL = GlobalVariables.serverProtocol + "://" + GlobalVariables.serverName + ":" + GlobalVariables.serverPort;

export default GlobalVariables;