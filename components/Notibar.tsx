import { useRecoilState } from 'recoil';
import { notibarState as notibarState } from './Header';
import NotiItem from './NotiItem';
import { NotiData } from '../types/notiTypes';
import { BsCheck2Square } from 'react-icons/bs';
import styles from '../styles/Notibar.module.scss';

export default function Notibar() {
  const dummyData: NotiData[] = [
    { id: 1, type: '성사', message: null, time: '09:10', isChecked: false, myTeam: ['jabae'], enemyTeam: ['sujpark'] },
    { id: 2, type: '취소', message: null, time: '09:10', isChecked: false, myTeam: ['jabae'], enemyTeam: ['sujpark'] },
    { id: 3, type: '5분전', message: null, time: '09:10', isChecked: false, myTeam: ['jabae'], enemyTeam: ['sujpark'] },
    { id: 4, type: '공지', message: '탁구대 망가짐으로 1시간 동안 경기가 불가합니다', time: '2022-05-31', isChecked: true },
  ];
  const [showNotibar, setShowNotibar] = useRecoilState<boolean>(notibarState);

  const allNotiDeleteHandler = () => {};

  return (
    <div className={styles.shadow} onClick={() => setShowNotibar(false)}>
      <div className={styles.notiWrap} onClick={(e) => e.stopPropagation()}>
        <span onClick={allNotiDeleteHandler}>
          <BsCheck2Square />
          전체삭제
        </span>
        <div>
          {dummyData.map((data: NotiData) => {
            return <NotiItem key={data.id} data={data} />;
          })}
        </div>
      </div>
    </div>
  );
}
