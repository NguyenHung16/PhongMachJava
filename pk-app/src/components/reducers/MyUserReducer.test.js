import cookie from 'react-cookies';
import { MyUserReducer } from './MyUserReducer';
import { patientUser } from '../../test-utils/fixtures';

jest.mock('react-cookies', () => ({
  remove: jest.fn(),
}));

describe('MyUserReducer', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('stores user on login', () => {
    expect(MyUserReducer(null, { type: 'login', payload: patientUser })).toBe(patientUser);
  });

  test('removes token and clears user on logout', () => {
    expect(MyUserReducer(patientUser, { type: 'logout' })).toBeNull();
    expect(cookie.remove).toHaveBeenCalledWith('token');
  });

  test('returns current user for unknown actions', () => {
    expect(MyUserReducer(patientUser, { type: 'unknown' })).toBe(patientUser);
  });
});
