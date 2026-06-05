import { render, screen } from '@testing-library/react';
import Footer from './Footer';
import MySpinner from './MySpinner';

test('renders footer contact information', () => {
  render(<Footer />);

  expect(screen.getByText(/contact@phongkhamqh.com/i)).toBeInTheDocument();
  expect(screen.getByText(/1900 1234/i)).toBeInTheDocument();
});

test('renders shared spinner', () => {
  const { container } = render(<MySpinner />);

  expect(container.querySelector('.spinner-grow')).toBeInTheDocument();
});
