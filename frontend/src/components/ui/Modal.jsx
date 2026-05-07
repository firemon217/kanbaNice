import { X } from 'lucide-react';
import { useEffect } from 'react';

export const Modal = ({ isOpen, onClose, title, children }) => {
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <div className="">
      {/* Backdrop */}
      <div 
        className=""
        onClick={onClose}
      />
      
      {/* Modal panel */}
      <div className="">
        <div className="">
          <h2 className="">{title}</h2>
          <button 
            onClick={onClose}
            className=""
          >
            <X className="w-5 h-5" />
          </button>
        </div>
        
        <div className="">
          {children}
        </div>
      </div>
    </div>
  );
};
