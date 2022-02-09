import './index.css';

import * as serviceWorker from './serviceWorker';

import App from './App';
import React from 'react';
import ReactDOM from 'react-dom';
import { UserProvider } from './contexts/UserContext';
import './helpers/i18n';
//import { debugContextDevtool } from 'react-context-devtool';

const container = document.getElementById('root');
let accessToken = localStorage.getItem('access_token');
let questionnaireBody = localStorage.getItem('questionnaire_body');
window.addEventListener('message', (data) => {
    accessToken = data.data.accessToken;
    questionnaireBody = data.data.questionnaireBody;
    if (accessToken != null) {
        localStorage.setItem('access_token', accessToken);
    }
    if (questionnaireBody != null) {
        localStorage.setItem('questionnaire_body', questionnaireBody);
    }
});
ReactDOM.render(
    <React.StrictMode>
        <UserProvider>
            <App />
        </UserProvider>
    </React.StrictMode>,
    container,
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
