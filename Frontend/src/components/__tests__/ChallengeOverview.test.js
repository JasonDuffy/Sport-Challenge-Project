import ChallengeOverview from "../ChallengeOverview";
import { render, getChallengeData } from "@testing-library/react";


/*
test('fetch data from backend', async() => {
  const mockData = {cha: 'challenge'};
  const mockResponse = {
    ok: true,
    json: jest.fn().mockResolvedValue(mockData)
  };
  jest.spyOn(global, 'fetch').mockResolvedValue(mockResponse);
  
  const result = await getChallengeData();
  expect(result).toEqual(mockData);
});

global.fetch = jest.fn(() =>
    Promise.resolve({
        json: () => Promise.resolve()
    })
)


beforeEach(() => {
    fetch.mockClear();
});
*/

describe('challenge overview', () => {
  beforeEach(() => {
    fetch.resetMocks()
  })
  
  it('testing something...', () => {
    fetch.mockResponseOnce(JSON.stringify({ data: '12345' }))
    const container = render (<ChallengeOverview/>)
    // expect statement can be wrong.
    expect(container.getByText(/12345/).toBeDefined())
  })
});