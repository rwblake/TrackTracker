export class Registration {
  constructor(
    public login: string,
    public email: string,
    public password: string,
    public spotifyID: string,
    public bio: string | null,
    public credentials: {
      accessToken: string;
      tokenType: string;
      scope: string;
      expiresIn: number;
      refreshToken: string;
    },
    public langKey: string
  ) {}
}
