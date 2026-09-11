import type { Course } from '../types/course';
import type { LoadState } from '../api/useCourses';

interface CourseListProps {
  courses: Course[];
  state: LoadState;
  errorMessage: string;
  onRetry: () => void;
  onEdit: (course: Course) => void;
  onDelete: (course: Course) => void;
}

export default function CourseList({
  courses,
  state,
  errorMessage,
  onRetry,
  onEdit,
  onDelete,
}: CourseListProps) {
  if (state === 'loading') return <p>Dang tai danh sach mon hoc...</p>;

  if (state === 'error') {
    return (
      <div style={{ color: '#b91c1c' }}>
        <p>{errorMessage}</p>
        <button onClick={onRetry} style={{ padding: '6px 12px', cursor: 'pointer' }}>
          Thu lai
        </button>
      </div>
    );
  }

  if (state === 'empty') return <p>Khong tim thay mon hoc nao phu hop.</p>;

  return (
    <table style={{ width: '100%', borderCollapse: 'collapse' }}>
      <thead>
        <tr style={{ textAlign: 'left', borderBottom: '2px solid #333' }}>
          <th style={{ padding: '8px' }}>Ten mon hoc</th>
          <th style={{ padding: '8px' }}>So tin chi</th>
          <th style={{ padding: '8px' }}>So cho con lai</th>
          <th style={{ padding: '8px' }}>Thao tac</th>
        </tr>
      </thead>
      <tbody>
        {courses.map((course) => (
          <tr key={course.id} style={{ borderBottom: '1px solid #eee' }}>
            <td style={{ padding: '8px' }}>{course.tenMonHoc}</td>
            <td style={{ padding: '8px' }}>{course.soTinChi}</td>
            <td
              style={{
                padding: '8px',
                color: course.soChoConLai === 0 ? '#b91c1c' : 'inherit',
                fontWeight: course.soChoConLai === 0 ? 'bold' : 'normal',
              }}
            >
              {course.soChoConLai} / {course.soChoToiDa}
            </td>
            <td style={{ padding: '8px' }}>
              <button
                onClick={() => onEdit(course)}
                style={{ padding: '4px 10px', cursor: 'pointer' }}
              >
                Sua
              </button>
              <button
                onClick={() => onDelete(course)}
                style={{
                  marginLeft: 8,
                  padding: '4px 10px',
                  color: '#b91c1c',
                  cursor: 'pointer',
                }}
              >
                Xoa
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
