// 1. react
import { useState } from 'react';
// 2. library
// 3. api
import { createComment } from '@/apis/requirement';
import { ThreadInfo } from '@/types/requirementTypes';
// 4. store
import usePageInfoStore from '@/stores/pageInfoStore';
// 5. component
import Profile from '@/components/layout/Profile';
// 6. asset
import Locked from '@/assets/icons/lock-closed.svg?react';
import Unlocked from '@/assets/icons/lock-open.svg?react';
import Pencil from '@/assets/icons/pencil.svg?react';

// api로 받아온 스레드 타입 설정
interface ThreadProps {
  data: ThreadInfo[];
  selected: boolean;
}

const Thread = ({ data, selected }: ThreadProps) => {
  const { projectId, requirementId } = usePageInfoStore();
  const [commentInput, setCommentInput] = useState('');

  const handleSendClick = () => {
    if (commentInput.trim() === '') {
      return;
    }
    // 댓글 작성 api 호출
    const commentData = {
      content: commentInput,
      receiverInfo: {
        isMentioned: true,
        receiverId: 'SSAFY1002',
        receiverName: '심규영',
      },
      projectId: projectId,
      requirementId: requirementId,
    };
    createComment(data[0].userThreadId, commentData);
    setCommentInput('');
  };

  return (
    <>
      {data.length > 0 && (
        <div
          className={`mb-4 flex max-h-[50vh] w-full flex-col justify-center rounded bg-white p-4 shadow transition duration-500 ${selected ? 'mb-6 -translate-x-3 scale-105 border shadow-lg' : ''}`}>
          <div className='flex w-full items-center justify-between border-b pb-3 text-lg font-normal'>
            {/* 타이틀 */}
            <div className='flex items-center'>
              <Profile
                width='2.5rem'
                height='2.5rem'
                name={data[0].threadAuthorInfo.name}
                profileColor={data[0].threadAuthorInfo.profileColor}
              />
              <div className='ml-1'>의 스레드</div>
            </div>

            {/* 잠금 여부 */}
            {data[0].isLocked === 'y' && (
              <>
                <div className='flex h-8 w-8 shrink-0 items-center justify-center rounded border-2 border-gray-200 bg-gray-50'>
                  <Locked className='h-4 w-4 stroke-1' />
                </div>
              </>
            )}
            {data[0].isLocked === 'n' && (
              <>
                <div className='flex h-8 w-8 shrink-0 items-center justify-center rounded border-2 border-gray-200 bg-gray-50'>
                  <Unlocked className='h-4 w-4 stroke-1' />
                </div>
              </>
            )}
          </div>

          {/* 댓글 조회 */}
          <ul className='my-5 max-h-72 w-full overflow-y-auto px-3'>
            {data[0].commentList.length === 0 && (
              <p className='text-center'>댓글이 없습니다.</p>
            )}
            {data[0].commentList.map((comment) => (
              <li
                key={comment.commentId}
                className='mb-6 flex items-center last-of-type:mb-0'>
                <Profile
                  width='2rem'
                  height='2rem'
                  name={comment.commentAuthorInfo.name}
                  profileColor={comment.commentAuthorInfo.profileColor}
                />
                <div className='mx-2 w-full rounded-md bg-gray-50 p-2 text-sm'>
                  {comment.content}
                </div>
              </li>
            ))}
          </ul>

          {/* 댓글 작성 */}
          {data[0].isLocked === 'n' && (
            <>
              <div className='flex w-full items-center'>
                <input
                  type='text'
                  placeholder='댓글 달기'
                  className='mr-2 h-10 w-full rounded border-2 border-gray-200 p-2 focus:outline-pubble'
                  value={commentInput}
                  onChange={(e) => setCommentInput(e.target.value)}
                />
                <button
                  className='flex h-9 w-9 shrink-0 items-center justify-center rounded bg-pubble p-2 text-white hover:bg-dpubble hover:outline-double hover:outline-4 hover:outline-gray-200'
                  onClick={handleSendClick}>
                  <Pencil />
                </button>
              </div>
            </>
          )}
        </div>
      )}
    </>
  );
};

export default Thread;