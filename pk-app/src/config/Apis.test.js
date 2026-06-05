import axios from 'axios';
import cookie from 'react-cookies';
import Apis, { authApis, endpoints, getImageUrl } from './Apis';

jest.mock('axios', () => ({
  create: jest.fn((config) => ({ config })),
}));

jest.mock('react-cookies', () => ({
  load: jest.fn(),
}));

describe('Apis config', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    axios.create.mockImplementation((config) => ({ config }));
  });

  test('creates a default API client with base URL', () => {
    expect(Apis.config.baseURL).toBe('http://localhost:8080/App/api');
  });

  test('builds authenticated API client with token header', () => {
    cookie.load.mockReturnValue('jwt-token');

    const client = authApis();

    expect(cookie.load).toHaveBeenCalledWith('token');
    expect(client.config.headers.Authorization).toBe('Bearer jwt-token');
    expect(axios.create).toHaveBeenCalledWith(expect.objectContaining({
      baseURL: 'http://localhost:8080/App/api',
      headers: { Authorization: 'Bearer jwt-token' },
    }));
  });

  test('normalizes image URLs', () => {
    expect(getImageUrl(null)).toContain('gravatar.com');
    expect(getImageUrl('https://cdn.example.com/avatar.png')).toBe('https://cdn.example.com/avatar.png');
    expect(getImageUrl('/uploads/avatar.png')).toBe('http://localhost:8080/uploads/avatar.png');
  });

  test('exposes endpoint builders used by components', () => {
    expect(endpoints['update-profile'](5)).toBe('/users/5');
    expect(endpoints['medical-records-patient'](7)).toBe('/patients/7/medical-records');
    expect(endpoints['pay-bill'](9)).toBe('/payments/bills/9/pay');
  });
});
