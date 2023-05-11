/*import ChallengeOverview from "../ChallengeOverview";

beforeEach(() => {
  fetch.resetMocks();
});

it('Return score', async () => {
  fetch.mockResponseOnce(JSON.stringify({
    result: [
      {
        user: 'John Doe',
        score: 42,
      }],
  }));
  const res = await ChallengeOverview.getChallengeData();
  expect(res).toEqual([{ score: 42, user: 'John Doe' }]);
  expect(fetch.mock.calls.length).toEqual(1);
});*/