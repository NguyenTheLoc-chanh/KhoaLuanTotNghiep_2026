import { Button, Typography, Container, Box } from '@mui/material'

export default function HomePage() {
  return (
    <Container>
      <Box sx={{ my: 4 }}>
        <Typography variant="h6" component="h1" gutterBottom>
            {import.meta.env.VITE_APP_NAME}
        </Typography>   
        <Typography variant="h4" component="h1" gutterBottom>
          Welcome to Tuyển Dụng with AI
        </Typography>
        <Button variant="contained" color="primary">
          Made by Trang, Dung, Loc with love
        </Button>
      </Box>
    </Container>
  )
}