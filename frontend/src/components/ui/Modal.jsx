import { useEffect } from 'react';
import {Button} from './elements/Button';

import modal from './Modal.module.css';

export const Modal = ({ isOpen, onClose, title, children,}) => {
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
    <div className={modal.overlay}>
      <div
        className={modal.backdrop}
        onClick={onClose}
      />

      <div className={modal.modal}>
        <div className={modal.header}>
          <h2 className={modal.title}>
            {title}
          </h2>

          <Button
            variant="close"
            className={modal.closeButton}
            onClick={onClose}
          >
            <span>X</span>
          </Button>
        </div>

        {/* CONTENT */}
        <div className={modal.content}>
          {children}
        </div>
      </div>
    </div>
  );
};