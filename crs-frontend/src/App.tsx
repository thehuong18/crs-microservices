import { useEffect, useState } from 'react';
import { getCourses } from './api/courseApi';
import type { Course } from './types/course';

function App() {
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getCourses()
      .then((res) => {
        setCourses(res.data.content);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <div style={{ padding: '20px' }}>Đang tải...</div>;
  if (error) return <div style={{ padding: '20px', color: 'red' }}>Lỗi: {error}</div>;

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial' }}>
      <h1>Danh sách môn học (qua Gateway)</h1>
      <ul>
        {courses.map((c) => (
          <li key={c.id}>
            <strong>{c.tenMonHoc}</strong> — {c.soTinChi} TC — Còn {c.soChoConLai}/{c.soChoToiDa} chỗ
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;