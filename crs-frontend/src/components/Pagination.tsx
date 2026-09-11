interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export default function Pagination({ currentPage, totalPages, onPageChange }: PaginationProps) {
  if (totalPages <= 1) return null;

  const pages = Array.from({ length: totalPages }, (_, i) => i);

  return (
    <div style={{ display: 'flex', gap: 6, marginTop: 16, alignItems: 'center' }}>
      <button
        disabled={currentPage === 0}
        onClick={() => onPageChange(currentPage - 1)}
        style={{ padding: '4px 10px', cursor: currentPage === 0 ? 'not-allowed' : 'pointer' }}
      >
        Trang truoc
      </button>

      {pages.map((p) => (
        <button
          key={p}
          onClick={() => onPageChange(p)}
          style={{
            padding: '4px 10px',
            fontWeight: p === currentPage ? 'bold' : 'normal',
            textDecoration: p === currentPage ? 'underline' : 'none',
            cursor: 'pointer',
          }}
        >
          {p + 1}
        </button>
      ))}

      <button
        disabled={currentPage >= totalPages - 1}
        onClick={() => onPageChange(currentPage + 1)}
        style={{
          padding: '4px 10px',
          cursor: currentPage >= totalPages - 1 ? 'not-allowed' : 'pointer',
        }}
      >
        Trang sau
      </button>
    </div>
  );
}
