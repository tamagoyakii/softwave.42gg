import { useState } from 'react';
import { CurrentGameInfo, GameResult } from 'types/scoreTypes';
import { MatchTeamsInfo } from './MatchTeamsInfo';
import Score from './Score';
import { Buttons } from './Buttons';
import Guide, { GuideLine } from './Guide';
import styles from 'styles/modal/AfterGameModal.module.scss';

const defaultResult: GameResult = { myTeamScore: '', enemyTeamScore: '' };

interface RankGameModalProps {
  currentGameInfo: CurrentGameInfo;
  guideLine: GuideLine;
  onSubmit: (gameResult: GameResult) => void;
}

export default function RankGameModal({
  currentGameInfo,
  guideLine,
  onSubmit,
}: RankGameModalProps) {
  const [result, setResult] = useState<GameResult>(defaultResult);
  const [onCheck, setOnCheck] = useState<boolean>(false);

  const inputScoreHandler = ({
    target: { name, value },
  }: React.ChangeEvent<HTMLInputElement>) => {
    const filteredValue = value.replace(/[^0-9]/g, '');
    setResult((prev) => ({
      ...prev,
      [name]: filteredValue === '' ? filteredValue : parseInt(filteredValue),
    }));
  };

  const enterHandler = () => {
    const { myTeamScore, enemyTeamScore } = result;
    if (isCorrectScore(myTeamScore, enemyTeamScore)) setOnCheck(true);
  };

  const resetHandler = () => {
    setResult((prev) => ({ ...prev, myScore: '', enemyScore: '' }));
    setOnCheck(false);
  };

  return (
    <div className={styles.container}>
      <Guide condition={onCheck} guideLine={guideLine} />
      <div className={styles.resultContainer}>
        <MatchTeamsInfo matchTeamsInfo={currentGameInfo.matchTeamsInfo} />
        <Score onCheck={onCheck} result={result} onChange={inputScoreHandler} />
      </div>
      <Buttons
        onCheck={onCheck}
        onEnter={enterHandler}
        onReset={resetHandler}
        onSubmit={() => onSubmit(result)}
      />
    </div>
  );
}

const isCorrectScore = (score1: number | '', score2: number | '') => {
  if (score1 === '' || score2 === '') {
    alert('점수를 입력해주세요.');
    return false;
  } else if (score1 > 2 || score2 > 2) {
    alert('점수로 3점이상 입력이 불가합니다! (3판 2선승제)');
    return false;
  } else if (score1 === score2) {
    alert('동점 입력은 불가합니다. 1점 내기로 승부를 결정해주세요!');
    return false;
  }
  return true;
};
