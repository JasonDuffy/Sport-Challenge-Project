import React from 'react';
import ChallengeOverview from './ChallengeOverview';
import './css/ChallengeList.css';

function ChallengeList() {
  return (
    <ul className="col challenge_list">
      <li className="challenge_list_item">
        <ChallengeOverview background="Challenge" />
      </li>
      <li className="challenge_list_item">
        <ChallengeOverview background="Challenge" />
      </li>
      <li className="challenge_list_item">
        <ChallengeOverview background="Challenge" />
      </li>
    </ul>
  );
}

export default ChallengeList;