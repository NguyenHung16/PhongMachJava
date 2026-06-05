import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { MyUserContext } from '../config/MyContexts';

export const renderWithProviders = (
  ui,
  {
    user = null,
    dispatch = jest.fn(),
    route = '/',
    routerState,
  } = {}
) => {
  window.history.pushState({}, 'Test page', route);

  return {
    dispatch,
    ...render(
      <MyUserContext.Provider value={[user, dispatch]}>
        <MemoryRouter initialEntries={[routerState ? { pathname: route, state: routerState } : route]}>
          {ui}
        </MemoryRouter>
      </MyUserContext.Provider>
    ),
  };
};
