import { useNavigate, Link as RouterLink } from "react-router-dom";
import { 
  Container, 
  Box, 
  Typography, 
  Button, 
  Stack,
  Paper 
} from "@mui/material";
import { Home, ArrowBack } from "@mui/icons-material";

export default function NotFound() {
  const navigate = useNavigate();

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          py: 4
        }}
      >
        <Paper
          elevation={3}
          sx={{
            p: 4,
            textAlign: 'center',
            width: '100%'
          }}
        >
          <Typography
            variant="h1"
            component="h1"
            sx={{
              fontSize: '6rem',
              fontWeight: 'bold',
              color: 'primary.main',
              mb: 2
            }}
          >
            404
          </Typography>
          
          <Typography
            variant="h4"
            component="h2"
            gutterBottom
            sx={{ mb: 2 }}
          >
            Trang không tồn tại
          </Typography>
          
          <Typography
            variant="body1"
            color="text.secondary"
            sx={{ mb: 4 }}
          >
            Xin lỗi, trang bạn đang tìm kiếm không tồn tại hoặc đã được di chuyển.
          </Typography>
          
          <Stack
            direction={{ xs: 'column', sm: 'row' }}
            spacing={2}
            justifyContent="center"
          >
            <Button
              variant="outlined"
              startIcon={<ArrowBack />}
              onClick={() => navigate(-1)}
              size="large"
            >
              Quay lại
            </Button>
            
            <Button
              variant="contained"
              startIcon={<Home />}
              component={RouterLink}
              to="/"
              size="large"
            >
              Về trang chủ
            </Button>
          </Stack>
        </Paper>
      </Box>
    </Container>
  );
}